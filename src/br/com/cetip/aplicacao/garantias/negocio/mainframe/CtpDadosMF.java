package br.com.cetip.aplicacao.garantias.negocio.mainframe;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.cetip.infra.log.Logger;

public class CtpDadosMF {

   // Codigos dos retornos WTC
   private static final String INADIMPLENCIA = "INAD001";
   private static final String RETDESVINCULACAO = "RDVC001";
   private static final String RETVINCULACAO = "RVNC001";
   private static final String RESGATE = "RSGT001";
   private static final String NEGOCIACAO = "NEGC001";

   private final String CTPOPERATION_ERROR_XML = "XML mal formatado";

   private String indAcao;
   private String numAviso;
   private String codIF;
   private String numCesta;
   private String codResposta;
   private String datOperacao;
   private String codContaGarantido;
   private String status;
   private String indInadimplencia;

   private String codOperacao;
   private String tipoOperacao;
   private String numControleLancamento;
   private String origem;
   private String txtResposta;

   public CtpDadosMF(String xml) {
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "ctpGarantiasDescidaBean xml ==> " + xml);
      }

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder;

      try {
         builder = factory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
         Logger.error(this, e);
         throw new RuntimeException(CTPOPERATION_ERROR_XML);
      }

      Document document;

      try {
         document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
      } catch (SAXException e) {
         Logger.error(this, e);
         throw new RuntimeException(CTPOPERATION_ERROR_XML);
      } catch (IOException e) {
         Logger.error(this, e);
         throw new RuntimeException(CTPOPERATION_ERROR_XML);
      }

      Node root = document.getDocumentElement();

      if ("WTC".equals(root.getNodeName())) {
         origem = "WTC";
         NodeList childNodes = root.getChildNodes();
         Node currentNode;
         for (int i = 0; i < childNodes.getLength(); i++) {
            currentNode = childNodes.item(i);

            if ("indAcao".equals(currentNode.getNodeName())) {
               indAcao = currentNode.getFirstChild().getNodeValue();
            }

            if ("numAviso".equals(currentNode.getNodeName())) {
               numAviso = currentNode.getFirstChild().getNodeValue();
            }

            if ("codIF".equals(currentNode.getNodeName())) {
               codIF = currentNode.getFirstChild().getNodeValue();
            }

            if ("numCesta".equals(currentNode.getNodeName())) {
               numCesta = currentNode.getFirstChild().getNodeValue();
            }

            if (this.getIndAcao().equalsIgnoreCase(NEGOCIACAO) && "codContaGarantido".equals(currentNode.getNodeName())) {
               codContaGarantido = currentNode.getFirstChild().getNodeValue();
            } else if (this.getIndAcao().equalsIgnoreCase(RETVINCULACAO)
                  || this.getIndAcao().equalsIgnoreCase(RETDESVINCULACAO)) {
               if ("codResposta".equals(currentNode.getNodeName())) {
                  codResposta = currentNode.getFirstChild().getNodeValue();
               }

               if ("txtResposta".equals(currentNode.getNodeName())) {
                  txtResposta = currentNode.getFirstChild().getNodeValue();
               }
            }

            if ("datOperacao".equals(currentNode.getNodeName())) {
               datOperacao = currentNode.getFirstChild().getNodeValue();
            }
         }
      } else {
         origem = "AVISODESCE";
         NodeList childNodes = root.getChildNodes();
         Node currentNode;

         for (int i = 0; i < childNodes.getLength(); i++) {
            currentNode = childNodes.item(i);
            indAcao = " ";

            if ("codOperacao".equals(currentNode.getNodeName())) {
               codOperacao = currentNode.getFirstChild().getNodeValue();
            }

            if ("tipoOperacao".equals(currentNode.getNodeName())) {
               tipoOperacao = currentNode.getFirstChild().getNodeValue();
            }

            if ("codIF".equals(currentNode.getNodeName())) {
               codIF = currentNode.getFirstChild().getNodeValue();
            }

            if ("numCesta".equals(currentNode.getNodeName())) {
               numCesta = currentNode.getFirstChild().getNodeValue();
            }

            if ("statusOperacao".equals(currentNode.getNodeName())) {
               codResposta = currentNode.getFirstChild().getNodeValue();
            }

            if ("numControleLancamento".equals(currentNode.getNodeName())) {
               numControleLancamento = currentNode.getFirstChild().getNodeValue();
            }

            if ("datOperacao".equals(currentNode.getNodeName())) {
               datOperacao = currentNode.getFirstChild().getNodeValue();
            }
         }
      }
   }

   public String getOrigem() {
      return origem;
   }

   public String getIndAcao() {
      return indAcao;
   }

   public String getNumAviso() {
      return numAviso;
   }

   public String getCodIF() {
      return codIF;
   }

   public String getNumCesta() {
      return numCesta;
   }

   public String getCodResposta() {
      return codResposta;
   }

   public String getTxtResposta() {
      return txtResposta;
   }

   public String getStatus() {
      return status;
   }

   public String getCodOperacao() {
      return codOperacao;
   }

   public String getTipoOperacao() {
      return tipoOperacao;
   }

   public String getNumControleLancamento() {
      return numControleLancamento;
   }

   public void setOrigem(String origem) {
      this.origem = origem;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getDataOperacao() {
      return datOperacao;
   }

   public String getCodContaGarantido() {
      return codContaGarantido;
   }

   public void setIndInadimplencia(String indInadimplencia) {
      this.indInadimplencia = indInadimplencia;
   }

   public String getIndInadimplencia() {
      return indInadimplencia;
   }

   public void setCodOperacao(String codOperacao) {
      this.codOperacao = codOperacao;
   }

   public void setTipoOperacao(String tipoOperacao) {
      this.tipoOperacao = tipoOperacao;
   }

   public void setNumControleLancamento(String numControleLancamento) {
      this.numControleLancamento = numControleLancamento;
   }

   public boolean ehNegociacao() {
      return indAcao.equals(NEGOCIACAO);
   }

   public boolean ehResgate() {
      return indAcao.equals(RESGATE);
   }

   public boolean ehRetornoVinculacao() {
      return indAcao.equals(RETVINCULACAO);
   }

   public boolean ehRetornoDesvinculacao() {
      return indAcao.equals(RETDESVINCULACAO);
   }

   public boolean ehInadimplencia() {
      return indAcao.equals(INADIMPLENCIA);
   }
}

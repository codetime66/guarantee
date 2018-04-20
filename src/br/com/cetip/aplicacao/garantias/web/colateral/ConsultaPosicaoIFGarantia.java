package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoObterCombosTipoInstrumentoFinanceiro;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultaPosicaoIFGarantia;
import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoValidaConsultaPosicaoIFGarantia;
import br.com.cetip.aplicacao.garantias.servico.colateral.ResultadoServicoConsultaPosicaoIFGarantia;
import br.com.cetip.aplicacao.garantias.web.AbstractFormularioGarantias;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.data.element.DataElement;

public class ConsultaPosicaoIFGarantia extends AbstractFormularioGarantias {
	 private NumeroInteiro qtdeLinhas = new NumeroInteiro(0);

	 public void entrada(GrupoDeGrupos layout, Grupo dados, Servicos servico)throws Exception {
      
		 RequisicaoServicoObterCombosTipoInstrumentoFinanceiro req = new RequisicaoServicoObterCombosTipoInstrumentoFinanceiro();
		 req.atribuirTIPO_INSTRUMENTO_FINANCEIRO_Texto(new Texto("CONTRATO"));
		 ResultadoServicoObterCombosTipoInstrumentoFinanceiro res = (ResultadoServicoObterCombosTipoInstrumentoFinanceiro) servico.executarServico(req);
		 
		GrupoDeGrupos geral = layout.grupoDeGrupos(1);
	    geral.posicaoTitulo(GrupoDeAtributos.TITULO_A_ESQUERDA);
        //Dados Garantido
		GrupoDeGrupos dadosFormGarantido = geral.grupoDeGrupos(1);

		GrupoDeAtributos att = dadosFormGarantido.grupoDeAtributos(1);
		att.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);
		att.contexto(Contexto.GARANTIDO);
		
	    att.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDO));
	    att.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO));
	    att.atributo(new Natureza(Contexto.NATUREZA_GARANTIDO));
	    att.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDO));	    
	   // att.atributo(res.obterTIPO_IF_GARANTIDO_CodigoTipoIF());
	    att.atributo(obterTipoIFGarantido());
	    att.atributo(new CodigoIF(Contexto.CODIGO_IF_GARANTIDO));	    
	    
	    //Dados Garantidor
        GrupoDeGrupos dadosFormGarantidor = layout.grupoDeGrupos(1);
	    att.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);
		
		
		GrupoDeAtributos attGarantidor = dadosFormGarantidor.grupoDeAtributos(1);
		attGarantidor.posicaoTitulos(GrupoDeAtributos.TITULOS_A_ESQUERDA);
		attGarantidor.contexto(Contexto.GARANTIDOR);
		
	    attGarantidor.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDOR));
	    attGarantidor.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDOR));
	    attGarantidor.atributo(new Natureza(Contexto.NATUREZA_GARANTIDOR));	    
	    attGarantidor.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDOR));	
	    attGarantidor.atributo(obterTipoIFGarantidor());
	   // attGarantidor.atributo(res.obterTIPO_IF_GARANTIDOR_CodigoTipoIF());
	    attGarantidor.atributo(new CodigoIF(Contexto.CODIGO_IF_GARANTIDOR));	    

   }

   public Class obterDestino(Grupo g, Servicos s) throws Exception {
      return RelacaoConsultaPosicaoIFGarantia.class;
   }

   public void validar(Grupo g, Servicos s) throws Exception {
      RequisicaoServicoValidaConsultaPosicaoIFGarantia req = new RequisicaoServicoValidaConsultaPosicaoIFGarantia();
      s.executarServico(req, g);
      RequisicaoServicoConsultaPosicaoIFGarantia reqCons = new RequisicaoServicoConsultaPosicaoIFGarantia();
      ResultadoServicoConsultaPosicaoIFGarantia res = (ResultadoServicoConsultaPosicaoIFGarantia)s.executarServico(reqCons,g);
      
      qtdeLinhas = (NumeroInteiro)res.obterQUANTIDADE_NumeroInteiro();
   }
  

   public boolean confirmacao(Grupo arg0, Servicos arg1) throws Exception {
	  return false;
   }
  
  /* (non-Javadoc)
	 * @see br.com.cetip.base.web.acao.Formulario#confirmacao(br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos, br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
	 */
	public void confirmacao(GrupoDeGrupos layout, Grupo dados, Servicos arg2)
		throws Exception {
	}
   
	private CodigoTipoIF obterTipoIFGarantido() throws Exception{
		
	      CodigoTipoIF codigoTipoIFGarantido = new CodigoTipoIF(Contexto.TIPO_IF_GARANTIDO);
	      DataElement.Domain domain = codigoTipoIFGarantido.getDomain();
	      domain.add(new CodigoTipoIF());	      
	      domain.add(CodigoTipoIF.CDB);
	      domain.add(CodigoTipoIF.CDCA);
	      domain.add(CodigoTipoIF.DI);
	      domain.add(CodigoTipoIF.DIM);
	      domain.add(CodigoTipoIF.LCA);
	      domain.add(CodigoTipoIF.OFCC);
	      domain.add(CodigoTipoIF.OFVC);
	      domain.add(CodigoTipoIF.SWAP);
	      domain.add(CodigoTipoIF.TCO);
	      domain.add(CodigoTipoIF.TIN);
	      domain.add(CodigoTipoIF.TMO);
	      
       return codigoTipoIFGarantido;	      
	}

	private CodigoTipoIF obterTipoIFGarantidor() {
	      CodigoTipoIF codigoTipoIFGarantidor = new CodigoTipoIF(Contexto.TIPO_IF_GARANTIDOR);
	      
			 DataElement.Domain domain = codigoTipoIFGarantidor.getDomain();
		      domain.add(new CodigoTipoIF());
		      domain.add(CodigoTipoIF.CCB);
		      domain.add(CodigoTipoIF.CCE);
		      domain.add(CodigoTipoIF.CDA);
		      domain.add(CodigoTipoIF.CDB);
		      domain.add(CodigoTipoIF.CDCA);
		      domain.add(CodigoTipoIF.CPR);
		      domain.add(CodigoTipoIF.CRP);
		      domain.add(CodigoTipoIF.CSEC);
		      domain.add(CodigoTipoIF.CTRA);
		      domain.add(CodigoTipoIF.DEB);
		      domain.add(CodigoTipoIF.DI);
		      domain.add(CodigoTipoIF.NCE);
		      domain.add(CodigoTipoIF.NCR);
		      domain.add(CodigoTipoIF.TDA);
		      domain.add(CodigoTipoIF.WA);
		      domain.add(CodigoTipoIF.LFT);
		      domain.add(CodigoTipoIF.LFTA);
		      domain.add(CodigoTipoIF.LFTB);
		      domain.add(CodigoTipoIF.LTN);
		      domain.add(CodigoTipoIF.NTNA);
		      domain.add(CodigoTipoIF.NTNB);
		      domain.add(CodigoTipoIF.NTNC);
		      domain.add(CodigoTipoIF.NTND);
		      domain.add(CodigoTipoIF.NTNE);
		      domain.add(CodigoTipoIF.NTNF);
		      domain.add(CodigoTipoIF.NTNH);
		      domain.add(CodigoTipoIF.NTNI);
		      domain.add(CodigoTipoIF.NTNL);
		      domain.add(CodigoTipoIF.NTNM);
		      domain.add(CodigoTipoIF.NTNP);
		      domain.add(CodigoTipoIF.NTNR);
		      domain.add(CodigoTipoIF.NTNS);
		      domain.add(CodigoTipoIF.NTNU);
		      
	       return codigoTipoIFGarantidor;
   }
	
   public NumeroInteiro getQtdeLinhas() {
	  	return qtdeLinhas;
   }
}
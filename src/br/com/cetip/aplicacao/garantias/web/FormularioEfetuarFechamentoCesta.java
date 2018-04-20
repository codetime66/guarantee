package br.com.cetip.aplicacao.garantias.web;

import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoConsultaCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoFechamentoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaFechamentoCestaGarantias;
import br.com.cetip.aplicacao.garantias.servico.ResultadoServicoConsultaCestaGarantias;
import br.com.cetip.base.web.acao.suporte.Notificacao;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeGrupos;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.atributo.tipo.texto.TextoLimitado;

/**
 * Formulario de Fechamento
 * 
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class FormularioEfetuarFechamentoCesta extends AbstractFormularioGarantias {

   private boolean possuiGarantido = false;

   private NumeroCestaGarantia numero;

   private CodigoContaCetip contaGarantido;

   /**
    * Consulta a Cesta
    * 
    * @param parametros
    * @param servicos
    * @return
    * @throws Exception
    */
   private ResultadoServicoConsultaCestaGarantias consultarCesta(Grupo parametros, Servicos servicos) throws Exception {
      numero = (NumeroCestaGarantia) parametros.obterAtributo(NumeroCestaGarantia.class, Contexto.GARANTIAS_CODIGO);

      RequisicaoServicoConsultaCestaGarantias req = null;
      req = new RequisicaoServicoConsultaCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      ResultadoServicoConsultaCestaGarantias res;
      res = (ResultadoServicoConsultaCestaGarantias) servicos.executarServico(req);

      contaGarantido = res.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      return res;
   }

   /**
    * Monta a tela
    * 
    * @param layout
    * @param parametros
    * @param servicos
    * @param res
    */
   private void montaTela(GrupoDeGrupos layout, Grupo parametros, ResultadoServicoConsultaCestaGarantias res) {
      NumeroCestaGarantia numCesta = (NumeroCestaGarantia) parametros.obterAtributo(NumeroCestaGarantia.class,
            Contexto.GARANTIAS_CODIGO);

      // Obter do resultado a conta da contraparte. Caso seja nula,
      // deve ser informada na tela.
      CodigoContaCetip parte = res.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      Nome nomeParte = res.obterGARANTIAS_PARTICIPANTE_Nome();
      Nome nomeContraParte = res.obterGARANTIAS_CONTRAPARTE_Nome();

      Data criacao = res.obterGARANTIAS_DATA_CRIACAO_Data();

      // Tela
      layout.contexto(Contexto.GARANTIAS_CESTA);
      GrupoDeGrupos principal = layout.grupoDeGrupos(1);
      principal.contexto(Contexto.GARANTIAS_CESTA);
      GrupoDeAtributos grupoCesta = principal.grupoDeAtributos(2);

      grupoCesta.atributoNaoEditavel(numCesta);
      grupoCesta.atributoNaoEditavel(criacao);

      // Campos
      grupoCesta.atributoNaoEditavel(parte);
      grupoCesta.atributoNaoEditavel(nomeParte);
      if (contaGarantido == null) {
         grupoCesta.atributo(new CodigoContaCetip(Contexto.GARANTIAS_CONTRAPARTE));
         possuiGarantido = false;
      } else {
         grupoCesta.atributoNaoEditavel(contaGarantido);
         grupoCesta.atributoNaoEditavel(nomeContraParte);
         possuiGarantido = true;
      }
   }

   /**
    * Metodo de entrada
    */
   public void entrada(GrupoDeGrupos layout, Grupo parametros, Servicos servicos) throws Exception {
      ResultadoServicoConsultaCestaGarantias res = consultarCesta(parametros, servicos);

      if (res.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip() != null) {
         return;
      }

      montaTela(layout, parametros, res);
   }

   /**
    * Valida se pode fechar
    */
   public void validar(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoValidaFechamentoCestaGarantias req;
      req = new RequisicaoServicoValidaFechamentoCestaGarantias();

      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);

      if (!possuiGarantido) {
         req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip((CodigoContaCetip) dados.obterAtributo(
               CodigoContaCetip.class, Contexto.GARANTIAS_CONTRAPARTE));
      }

      servicos.chamarServico(req);
   }

   /**
    * Sempre confirma
    */
   public boolean confirmacao(Grupo parametros, Servicos servicos) throws Exception {
      return true;
   }

   /**
    * Monta tela de confirmacao
    */
   public void confirmacao(GrupoDeGrupos layout, Grupo parametros, Servicos servicos) throws Exception {
      ResultadoServicoConsultaCestaGarantias res = consultarCesta(parametros, servicos);

      if (contaGarantido != null) {
         montaTela(layout, parametros, res);
      }
   }

   /**
    * chamarServico
    */
   public Notificacao chamarServico(Grupo dados, Servicos servicos) throws Exception {
      RequisicaoServicoFechamentoCestaGarantias req;

      if (!possuiGarantido) {
         contaGarantido = (CodigoContaCetip) dados
               .obterAtributo(CodigoContaCetip.class, Contexto.GARANTIAS_CONTRAPARTE);
      }

      // muda para Em Finalizacao
      req = new RequisicaoServicoFechamentoCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contaGarantido);
      req.atribuirGARANTIAS_CESTA_Booleano(new Booleano(Booleano.VERDADEIRO));
      servicos.chamarServico(req);

      // lanca o mig
      req = new RequisicaoServicoFechamentoCestaGarantias();
      req.atribuirGARANTIAS_CODIGO_NumeroCestaGarantia(numero);
      req.atribuirGARANTIAS_CONTRAPARTE_CodigoContaCetip(contaGarantido);
      req.atribuirGARANTIAS_CESTA_Booleano(new Booleano(Booleano.FALSO));
      servicos.chamarServicoAssincrono(req);

      Notificacao not = new Notificacao("FechamentoCestaGarantiasPorFormulario.Sucesso");

      // marreta para exibir num cesta com 8 posicoes
      TextoLimitado codCesta = new TextoLimitado(numero.obterConteudo().toString().length() < 8 ? "0"
            + numero.obterConteudo().toString() : numero.obterConteudo().toString());

      not.parametroMensagem(codCesta, 0);
      not.parametroMensagem(contaGarantido, 1);

      return not;
   }

}

package br.com.cetip.aplicacao.garantias.negocio;

import java.util.Calendar;

import br.com.cetip.aplicacao.garantias.apinegocio.ICestaDeGarantias;
import br.com.cetip.aplicacao.garantias.apinegocio.IValidacaoVinculacaoTitulo;
import br.com.cetip.aplicacao.garantias.servico.RequisicaoServicoValidaVinculacaoCestaGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TituloDO;
import br.com.cetip.infra.atributo.AtributosColunados;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoFormaPagamento;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroControleLancamento;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.motorderegra.nucleo.motor.FabricaDeMotorDeRegra;
import br.com.cetip.negocio.regra.ConstantesDeNomeDeRegras;

abstract class ValidacaoVinculacaoTitulo extends ValidacaoVinculacao implements IValidacaoVinculacaoTitulo {

   public void validar(RequisicaoServicoValidaVinculacaoCestaGarantias req) {
      // valores
      CodigoContaCetip parte = req.obterGARANTIAS_PARTICIPANTE_CodigoContaCetip();
      CodigoContaCetip contraParte = req.obterGARANTIAS_CONTRAPARTE_CodigoContaCetip();
      NumeroCestaGarantia numero = req.obterGARANTIAS_CODIGO_NumeroCestaGarantia();
      Funcao lancador = req.obterGARANTIAS_TIPO_ACESSO_Funcao();
      CodigoTipoIF codigoTipoIf = req.obterGARANTIAS_CODIGO_TIPO_CodigoTipoIF();
      CodigoIF codigoIF = req.obterGARANTIAS_CODIGO_IF_CodigoIF();
      CodigoSistema sistema = req.obterGARANTIAS_SISTEMA_CodigoSistema();
      Booleano depositado = req.obterGARANTIAS_DEPOSITADO_Booleano();
      NumeroControleLancamento nrOperacao = req.obterGARANTIAS_QT_OPERACAO_NumeroControleLancamento();
      Quantidade qtOperacao = req.obterGARANTIAS_QT_OPERACAO_Quantidade();
      Quantidade puOperacao = req.obterGARANTIAS_PU_Quantidade();
      Id modalidade = req.obterMODALIDADE_LIQUIDACAO_Id();
      CPFOuCNPJ comitente = req.obterGARANTIAS_PARTICIPANTE_CPFOuCNPJ();

      // Data Atual
      Calendar cal = Calendar.getInstance();
      int hr = cal.get(Calendar.HOUR_OF_DAY);

      // Objeto de Negocio
      ICestaDeGarantias icg = getFactory().getInstanceCestaDeGarantias();
      CestaGarantiasDO cesta = icg.obterCestaDeGarantias(numero);

      InstrumentoFinanceiroDO numIF = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(codigoIF);

      // Valida se Tipo IF do IF possui IND_GARANTIDO = 'S'
      if (!validaTipoInstrumentoFinanceiroGarantido(numIF)) {
         throw new Erro(CodigoErro.CESTA_TIPO_IF_INVALIDO);
      }

      // Valida se nao esta vinculado a alguma outra cesta
      if (!validaInstrumentoFinanceiroGarantido(numIF)) {
         throw new Erro(CodigoErro.CESTA_IF_VINCULADO);
      }

      Id stCesta = cesta.getStatusCesta().getNumIdStatusCesta();
      stCesta.atribuirContexto(Contexto.GARANTIAS_STATUS);

      QuantidadeInteiraPositiva qtdRetirada = new QuantidadeInteiraPositiva("0");
      QuantidadeInteiraPositiva qtdEmitida = numIF.getQtdEmitida();
      Quantidade qtdDepositada = numIF.getQtdDepositada();
      if (numIF instanceof TituloDO) {
         TituloDO titulo = (TituloDO) numIF;
         qtdDepositada = new Quantidade(titulo.getQuantidadeDepositada().obterConteudo().toString());
         qtdEmitida = titulo.getQuantidadeEmitida();

         if (!Condicional.vazio(titulo.getQuantidadeRetirada())) {
            qtdRetirada.atribuirConteudo(titulo.getQuantidadeRetirada().obterConteudo().toString());
         }
      }

      if (Condicional.vazio(qtdDepositada)) {
         qtdDepositada = new Quantidade("0");
      }

      if (Condicional.vazio(qtdEmitida)) {
         qtdEmitida = new QuantidadeInteiraPositiva("0");
      }

      IdTipoFormaPagamento idFormaPgto = null;
      if (numIF.getFormaPagamento() != null) {
         idFormaPgto = numIF.getFormaPagamento().getNumIdFormaPagamento();
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Parte: " + parte);
         Logger.debug(this, "ContraParte: " + contraParte);
         Logger.debug(this, "Numero: " + numero);
         Logger.debug(this, "Tipo IF: " + codigoTipoIf);
         Logger.debug(this, "Codigo IF: " + codigoIF);
         Logger.debug(this, "Sistema: " + sistema);
         Logger.debug(this, "St Cesta: " + stCesta);
         Logger.debug(this, "Depositado: " + depositado);
         Logger.debug(this, "Nr Operacao: " + nrOperacao);
         Logger.debug(this, "Qt Operacao: " + qtOperacao);
         Logger.debug(this, "Pu Operacao: " + puOperacao);
         Logger.debug(this, "Modalidade: " + modalidade);
         Logger.debug(this, "Hora: " + hr);
         Logger.debug(this, "Comitente: " + comitente);
         Logger.debug(this, "Qtd Depositada: " + qtdDepositada);
      }

      AtributosColunados ac = new AtributosColunados();
      ac.atributo(parte);
      ac.atributo(contraParte);
      ac.atributo(numero);
      ac.atributo(lancador);
      ac.atributo(codigoTipoIf);
      ac.atributo(codigoIF);
      ac.atributo(depositado);
      ac.atributo(nrOperacao);
      ac.atributo(qtOperacao);
      ac.atributo(puOperacao);
      ac.atributo(modalidade);
      ac.atributo(stCesta);
      ac.atributo(new NumeroInteiro(hr));
      ac.atributo(comitente == null ? new CPFOuCNPJ() : comitente);
      ac.atributo(comitente == null ? Natureza.VAZIO : comitente.ehCNPJ() ? Natureza.PESS0A_JURIDICA
            : Natureza.PESS0A_FISICA);
      ac.atributo(sistema);
      ac.atributo(qtdDepositada);
      ac.atributo(idFormaPgto);
      ac.atributo(qtdEmitida);
      ac.atributo(qtdRetirada);

      try {
         FabricaDeMotorDeRegra.getMotorDeRegra().avalia(ConstantesDeNomeDeRegras.podeRegistrarVinculacaoCestaGarantias,
               ac, true);
      } catch (Exception e) {
         Logger.error(e);

         if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
         }

         throw new RuntimeException(e);
      }

      validarTiposGarantidoresValidos(cesta);

      validaNaturezaGarantido(cesta);
   }

}

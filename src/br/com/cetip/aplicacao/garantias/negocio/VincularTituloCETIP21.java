package br.com.cetip.aplicacao.garantias.negocio;

import br.com.cetip.aplicacao.garantias.apinegocio.IGarantidoCesta;
import br.com.cetip.aplicacao.garantias.apinegocio.IVincularTitulo;
import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoDepositoFactory;
import br.com.cetip.aplicacao.operacao.apinegocio.OperacaoDepositoVO;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasIFDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusCestaIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TituloDO;
import br.com.cetip.dados.aplicacao.sap.ContaParticipanteDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGrupoModalidadeLiquidacao;
import br.com.cetip.infra.atributo.tipo.numero.Preco;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;

class VincularTituloCETIP21 extends BaseGarantias implements IVincularTitulo {

   public void vincularTitulo(MovimentacaoGarantiaDO movVinculacao, CestaGarantiasIFDO vinculo) {
      Data dataHoje = getDataHoje();

      if (movVinculacao.getIndDepositado().ehFalso()) {
         // nao estah depositado. soh irah vincular apos a geracao e finalizacao do deposito
         TituloDO titulo = (TituloDO) movVinculacao.getInstrumentoFinanceiro();

         IGarantidoCesta iGarantido = getFactory().getInstanceGarantidoCesta();
         ContaParticipanteDO garantido = iGarantido.obterGarantidoCesta(movVinculacao.getCestaGarantias());
         CodigoContaCetip contaFavorecido = garantido.getCodContaParticipante();
         IdTipoGrupoModalidadeLiquidacao idGrpModalidade = new IdTipoGrupoModalidadeLiquidacao(movVinculacao
               .getModalidade().getGrupoModalidadeLiquidacao().getCodigo().toString());
         Preco puFavorecido = new Preco(movVinculacao.getPuOperacao());
         QuantidadeInteiraPositiva quantidade = new QuantidadeInteiraPositiva(movVinculacao.getQtdOperacao());

         CPFOuCNPJ comitente = movVinculacao.getCpfOuCnpjComitente();
         Natureza natComitente = null;
         if (!Condicional.vazio(comitente)) {
            natComitente = comitente.obterNatureza();
         }

         Id idCesta = movVinculacao.getCestaGarantias().getNumIdCestaGarantias();

         boolean lancamentoGarantido = false;
         if (movVinculacao.getCestaGarantias().getStatusCesta().equals(StatusCestaDO.VNC_PEND_GRTDO)) {
            lancamentoGarantido = true;
         }

         //Faz o deposito
         OperacaoDepositoVO depVO = new OperacaoDepositoVO();
         depVO.setTitulo(titulo);
         depVO.setCodContaFavorecido(contaFavorecido);
         depVO.setIdGrupoModalidade(idGrpModalidade);
         depVO.setValPUFavorecido(puFavorecido);
         depVO.setMeuNumero(movVinculacao.getNumControleLancamento());
         depVO.setMeuNumeroFavorecido(movVinculacao.getNumControleLancamento());
         depVO.setNumeroAssociacao(null); // nao tem associada para vinculacao
         depVO.setIdBancoLiquidante(null); // usa default
         depVO.setIdBancoLiquidanteFavorecido(null); // usa default
         depVO.setCpfOuCNPJComitente(comitente);
         depVO.setNaturezaComitente(natComitente);
         depVO.setQuantidade(quantidade);
         depVO.setIdCestaGarantias(idCesta);
         depVO.setLancamentoFavorecido(lancamentoGarantido);

         try {
            OperacaoDepositoFactory.getInstance().incluirDepositoCasado(depVO);
         } catch (Exception e) {
            if (e instanceof RuntimeException) {
               throw (RuntimeException) e;
            }

            Logger.error(this, e);
            throw new Erro(CodigoErro.ERRO, "Operacao: " + e.getMessage());
         }
      } else { // jah estah depositado. pode vincular ao ativo
         vinculo.setStatus(StatusCestaIFDO.VINCULADA_AO_ATIVO);
         vinculo.setDatAlteracao(dataHoje);
      }
   }

   public void registrar(TiposVinculacaoTitulo tipos) {
      tipos.registrar(SistemaDO.CETIP21, this);
   }
}
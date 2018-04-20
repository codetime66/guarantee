package br.com.cetip.aplicacao.garantias.negocio.colateral;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.colateral.IParametroIFGarantidor;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.ITipoIF;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.TipoIFFactory;
import br.com.cetip.aplicacao.sap.apinegocio.SistemaFactory;
import br.com.cetip.dados.aplicacao.garantias.HabilitaIFGarantidorDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFSistemaDO;
import br.com.cetip.dados.aplicacao.sca.SistemaDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.utilitario.Condicional;

public class ParametroIFGarantidor extends BaseGarantias implements IParametroIFGarantidor {

   public void incluirParamIFGarantidor(CodigoSistema codSistema, CodigoTipoIF codTipoIF, CodigoIF codigoIF) {
      HabilitaIFGarantidorDO ifGarantDO = new HabilitaIFGarantidorDO();
      InstrumentoFinanceiroDO ifDO = null;

      if (!Condicional.vazio(codigoIF)) {
         ifDO = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(codigoIF);
         ifGarantDO.setInstrumentoFinanceiro(ifDO);
      }
      SistemaDO sistDO = null;
      TipoIFDO tipoIFDO = null;
      try {
         sistDO = SistemaFactory.getInstance().obterSistema(codSistema);
         tipoIFDO = TipoIFFactory.getInstance().obterTipo(codTipoIF);
      } catch (Exception e) {
         throw new Erro(CodigoErro.ID_SISTEMA_INVALIDO);
      }

      ITipoIF iti = TipoIFFactory.getInstance();
      TipoIFSistemaDO tipoIfSistemaDO = iti.obterTipoIFSistema(sistDO.getNumero(), tipoIFDO.getNumTipoIF());

      ifGarantDO.setTipoIFSistema(tipoIfSistemaDO);
      ifGarantDO.setDataInclusao(getDataHoje());

      getGp().saveOrUpdate(ifGarantDO);
   }

   public Booleano existeCodigoIFJaHabilitado(CodigoSistema codSistema, CodigoTipoIF codTipoIF, CodigoIF codIF) {
      Booleano res = Booleano.FALSO;

      List paramIFGarantidor = getGp().find(
            "select count(*) from HabilitaIFGarantidorDO h where h.tipoIFSistema.tipoIF.codigoTipoIF = ? and "
                  + "h.tipoIFSistema.sistema.codSistema = ? and h.instrumentoFinanceiro.codigoIF = ?",
            new Object[] { codTipoIF, codSistema, codIF });

      Integer count = (Integer) paramIFGarantidor.get(0);
      if (count.intValue() == 1) {
         res = Booleano.VERDADEIRO;
      }

      return res;
   }

   public HabilitaIFGarantidorDO obtemParamIFGarantidorPorModuloSistema(CodigoSistema codSistema, CodigoTipoIF codTipoIF) {
      HabilitaIFGarantidorDO paramIFGarantidorDO = null;

      List lParamIF = getGp().find(
            "from HabilitaIFGarantidorDO h where h.tipoIFSistema.tipoIF.codigoTipoIF = ? and "
                  + "h.tipoIFSistema.sistema.codSistema = ?", new Object[] { codTipoIF, codSistema });

      if (!Condicional.vazio(lParamIF)) {
         paramIFGarantidorDO = (HabilitaIFGarantidorDO) lParamIF.get(0);
      }

      return paramIFGarantidorDO;
   }

   public Booleano ehHabilitadoPorModuloSistema(CodigoSistema codSistema, CodigoTipoIF codTipoIF) {
	      HabilitaIFGarantidorDO paramIFGarantidorDO = null;
	      Booleano res = Booleano.FALSO;

	      List lParamIF = getGp().find(
	            "from HabilitaIFGarantidorDO h where h.tipoIFSistema.tipoIF.codigoTipoIF = ? and "
	                  + "h.tipoIFSistema.sistema.codSistema = ? and h.instrumentoFinanceiro.id is null", new Object[] { codTipoIF, codSistema });

	      if (!Condicional.vazio(lParamIF)) {
	         res = Booleano.VERDADEIRO;
	      }

	      return res;
	}
   
   public void excluiParamIFGarantidor(CodigoSistema codSistema, CodigoTipoIF codTipoIF) {
      List lParamIF = getGp().find(
            " from " + HabilitaIFGarantidorDO.class.getName()
                  + "  h where h.tipoIFSistema.tipoIF.codigoTipoIF = ? and " + "h.tipoIFSistema.sistema.codSistema=?",
            new Object[] { codTipoIF, codSistema });

      if (lParamIF != null && lParamIF.size() > 0) {
         for (int i = 0; i < lParamIF.size(); i++) {
            HabilitaIFGarantidorDO IFGarantDO = (HabilitaIFGarantidorDO) lParamIF.get(i);
            getGp().delete(IFGarantDO);
         }
      }
   }
   
   public Booleano ehAtivoCetipComoAgenteCalculo(Id numIF ) {
	      Booleano res = Booleano.FALSO;

	      List agCalcVDO = getGp().find(
	            "select count(*) from AgenteCalculoVDO ag where ag.numContaParticipante =8419  and "
	                  + "ag.numIF= ?",
	            new Object[] { numIF });

	      Integer count = (Integer) agCalcVDO.get(0);
	      if (count.intValue() == 1) {
	         res = Booleano.VERDADEIRO;
	      }

	      return res;
   }
}

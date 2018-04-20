package br.com.cetip.aplicacao.garantias.apinegocio.agro;

import java.util.List;

import br.com.cetip.dados.aplicacao.garantias.HabilitaGarantidorDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFSistemaDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.texto.Nome;
import br.com.cetip.infra.persistencia.IConsulta;

public interface IIFGarantidor {

   public void incluirIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codIF)throws Exception;
   public void habilitarIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codIF)throws Exception;
   public void desabilitarIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codIF)throws Exception;
   public Booleano existeGarantidorHabilitado(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception;
   public HabilitaGarantidorDO obtemIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception;
   public void excluirIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception;
   public List obtemListaIFGarantidorHabilitado(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception;
   public Booleano podeExcluirIFGarantidorHabilitado(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception;
   public CodigoTipoIF obterComboTipoIF()throws Exception;
	         
}

package br.com.cetip.aplicacao.garantias.negocio;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.aplicacao.garantias.apinegocio.IRespostaTransferenciaCustodia;
import br.com.cetip.infra.atributo.tipo.identificador.SituacaoOperacaoSelic;
import br.com.cetip.infra.atributo.utilitario.Condicional;

final class TiposRespostaTransferenciaCustodia {
	
	public TiposRespostaTransferenciaCustodia() {
	      new RespostaTransferenciaCustodia().registrar(this);
	      new RepostaTransferenciaCesta().registrar(this);
	      new RespostaTransferenciaCustodiaExpirada().registrar(this);
	   }

	   private Map mapa = new HashMap();

	   public void registrar(SituacaoOperacaoSelic situacao, IRespostaTransferenciaCustodia instance) {
		   Object key = null;
			if (!Condicional.vazio(situacao)) {
				key = situacao.obterConteudo();
			}
			mapa.put(key, instance);
	   }

	   public IRespostaTransferenciaCustodia obter(SituacaoOperacaoSelic situacao) {
		  String key = null;
		  if ( !Condicional.vazio(situacao)){
			  key = situacao.obterConteudo();
		  }
	      Object o = mapa.get(key);
	      if (o == null) {
	         o = mapa.get(null);
	      }
	      return (IRespostaTransferenciaCustodia) o;
	   }

}

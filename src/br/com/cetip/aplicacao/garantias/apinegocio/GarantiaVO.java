package br.com.cetip.aplicacao.garantias.apinegocio;

import br.com.cetip.dados.aplicacao.garantias.IGarantiaCestaDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroOperacao;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.atributo.tipo.texto.Descricao;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;

public class GarantiaVO {

   public CodigoIF codIF;
   public InstrumentoFinanceiroDO ativoCetipado;
   public Texto codIfNCetipado;
   public Booleano indDireitoGarantidor;
   public Quantidade quantidade;
   public Descricao descricao;
   public NumeroOperacao numeroOperacao;

   public Atributo getCodGarantia() {
      if (isAtivoCetipado()) {
         return ativoCetipado != null ? ativoCetipado.getCodigoIF() : codIF;
      }

      return codIfNCetipado;
   }

   public boolean isAtivoCetipado() {
      boolean cetipadoPreenchido = !Condicional.vazio(ativoCetipado) || !Condicional.vazio(codIF);
      boolean naoCetipadoPreenchido = !Condicional.vazio(codIfNCetipado);

      if ((cetipadoPreenchido && naoCetipadoPreenchido) || (!cetipadoPreenchido && !naoCetipadoPreenchido)) {
         throw new IllegalArgumentException("Uma garantia deve ser cetipada ou nao cetipada");
      }

      return cetipadoPreenchido;
   }

   public void associarGarantia(IGarantiaCestaDO garantia) {
      if (isAtivoCetipado()) {
         garantia.setInstrumentoFinanceiro(ativoCetipado);
         garantia.setIndCetipado(Booleano.VERDADEIRO);
      } else {
         garantia.setCodIfNCetipado(codIfNCetipado);
         garantia.setIndCetipado(Booleano.FALSO);
      }
   }

   public void atribuirCodIFComTipoIF(CodigoIF _codIF, CodigoTipoIF codigoTipoIF) {
      if (codigoTipoIF.ehNAO_CETIPADO() || codigoTipoIF.ehN_CTP()) {
         this.codIfNCetipado = new Texto(_codIF.obterConteudo());
      } else {
         this.codIF = _codIF;
      }
   }

}

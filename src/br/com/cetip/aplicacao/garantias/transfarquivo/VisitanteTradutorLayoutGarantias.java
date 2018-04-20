package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.HashMap;
import java.util.Map;

import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.Funcao;
import br.com.cetip.infra.atributo.tipo.identificador.IdTipoGarantia;
import br.com.cetip.infra.atributo.visitante.tradutor.VisitanteTradutorLayoutPosicional;

/**
 *
 * @author <a href="mailto:bruno.borges@summa-tech.com">Bruno Borges</a>
 */
public class VisitanteTradutorLayoutGarantias extends VisitanteTradutorLayoutPosicional {

   public void visit(Booleano atributo) {
      Map map = new HashMap();
      map.put(" ", Booleano.VAZIO);
      map.put("S", Booleano.VERDADEIRO);
      map.put("N", Booleano.FALSO);
      map.put("V", Booleano.VERDADEIRO);
      map.put("D", Booleano.FALSO);
      setDestino(traduzirDominio(atributo, getOrigem(), map));
   }

   public void visit(Funcao atributo) {
      Map map = new HashMap();
      map.put("INCL", Funcao.INCLUSAO);
      map.put("ALTR", Funcao.ALTERACAO);
      map.put("EXCL", Funcao.EXCLUSAO);
      map.put("MANU", new Funcao("MANUTENCAO"));
      map.put("APTG", new Funcao("APORTE"));
      map.put("LIBG", new Funcao("LIBERACAO"));
      map.put("LIBP", new Funcao("LIBERACAO PARCIAL"));
      map.put("RETG", new Funcao("RETIRADA"));
      map.put("VINC", new Funcao("VINCULACAO"));
      map.put("FECH", new Funcao("FECHAMENTO"));
      map.put("VNCC", new Funcao("VINCULACAOCONTRATO"));
      map.put("N-CTP", new Funcao("NAO CETIPADO"));
      map.put("AUTZ", new Funcao("AUTORIZACAO"));
      map.put("DESZ", new Funcao("DESAUTORIZACAO"));

      setDestino(traduzirDominio(atributo, getOrigem(), map));
   }

   public void visit(IdTipoGarantia atributo) {
      Map map = new HashMap();
      map.put("", null);
      // map.put("1", IdTipoGarantia.REAL_PENHOR);
      map.put("2", IdTipoGarantia.CESSAO_FIDUCIARIA);
      map.put("3", IdTipoGarantia.PENHOR_NO_EMISSOR);
      setDestino(traduzirDominio(atributo, getOrigem(), map));
   }

}

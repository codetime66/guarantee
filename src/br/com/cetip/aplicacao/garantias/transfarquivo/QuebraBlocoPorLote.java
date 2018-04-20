/**
 * 
 */
package br.com.cetip.aplicacao.garantias.transfarquivo;

import java.util.List;

import br.com.cetip.infra.servico.arquivo.processador.ProcessadorArquivo;
import br.com.cetip.infra.servico.arquivo.quebrabloco.QuebraBloco;

class QuebraBlocoPorLote extends QuebraBloco {

   private int contagem = 0;

   private int lote = 0;

   public QuebraBlocoPorLote(ProcessadorArquivo pa, int tamanhoLote) {
      super(pa);
      lote = tamanhoLote;
   }

   public boolean mesmoBloco(List bloco, String novaLinha) {
      if (++contagem % lote == 0) {
         contagem = 0;
         return false;
      }

      return true;
   }

}
package br.com.cetip.aplicacao.garantias.negocio;

import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.IMovimentacoesGarantias;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.MovimentacaoGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.StatusMovimentacaoGarantiaDO;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.numero.Quantidade;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;

public class ExcluirGarantiaNaoCetip extends ExcluirGarantia {

   public void registrar(TiposExclusaoGarantia tp) {
      tp.registrar(CodigoTipoIF.NAO_CETIPADO, this);
   }

   
   public void excluirGarantia(DetalheGarantiaDO garantia) {
      IMovimentacoesGarantias imovs = getFactory().getInstanceMovimentacoesGarantias();

      
      
      MovimentacaoGarantiaDO mov = imovs.incluirMovimentacaoDesbloqueio(garantia);
      garantia.setQuantidadeGarantia(new Quantidade("0"));
      mov.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.OK);
      
      atualizaBloqueioGarantia(mov);

   }

   

   private void atualizaBloqueioGarantia(MovimentacaoGarantiaDO itemCestaDO) {
      IGerenciadorPersistencia gp = getGp();
      StringBuffer hqld = new StringBuffer();
      hqld.append("from ");
      hqld.append(MovimentacaoGarantiaDO.class.getName());
      hqld.append(" d where ");
      hqld.append(" d.codIfNCetipado = :codNCetip");
      hqld.append(" and d.indCetipado = :indCetipado");
      hqld.append(" and d.indDireitosGarantidor = :direitoGarantidor");
      hqld.append(" and d.cestaGarantias = :cesta ");
      hqld.append(" and d.statusMovimentacaoGarantia = :statusMovimentacaoGarantia ");
      hqld.append(" and d.qtdGarantia = :qtdGarantia ");
      
      

      IConsulta consulta = gp.criarConsulta(hqld.toString());
      consulta.setAtributo("codNCetip", itemCestaDO.getCodIfNCetipado());
      consulta.setAtributo("statusMovimentacaoGarantia", StatusMovimentacaoGarantiaDO.OK);
      consulta.setAtributo("cesta", itemCestaDO.getCestaGarantias());
      consulta.setAtributo("direitoGarantidor", itemCestaDO.getIndDireitosGarantidor());
      consulta.setAtributo("indCetipado", itemCestaDO.getIndCetipado());
      consulta.setAtributo("qtdGarantia", itemCestaDO.getQtdGarantia());
      
      

      List lista = consulta.list();
      if(!lista.isEmpty()){
    	  Logger.info("Atualizando a movimentação de Bloqueio");
    	  MovimentacaoGarantiaDO movimentacaoGarantiaDO = (MovimentacaoGarantiaDO)lista.get(0);
    	  movimentacaoGarantiaDO.setStatusMovimentacaoGarantia(StatusMovimentacaoGarantiaDO.MOVIMENTACAO_CANCELADA);
    	  gp.update(movimentacaoGarantiaDO);
      } else {
    	  Logger.info("movimentacao de bloqieio nao encontrada para operacao de desbloqueio");
      }

   }
}

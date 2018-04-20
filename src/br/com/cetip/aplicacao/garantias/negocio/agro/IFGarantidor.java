package br.com.cetip.aplicacao.garantias.negocio.agro;

import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.agro.IIFGarantidor;
import br.com.cetip.aplicacao.garantias.negocio.BaseGarantias;
import br.com.cetip.aplicacao.instrumentofinanceiro.apinegocio.InstrumentoFinanceiroFactory;
import br.com.cetip.dados.aplicacao.garantias.DetalheGarantiaDO;
import br.com.cetip.dados.aplicacao.garantias.HabilitaGarantidorDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.InstrumentoFinanceiroDO;
import br.com.cetip.dados.aplicacao.instrumentofinanceiro.TipoIFDO;
import br.com.cetip.dados.aplicacao.sca.UsuarioDO;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.texto.NomeAbreviado;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.persistencia.GerenciadorPersistenciaFactory;
import br.com.cetip.infra.persistencia.IConsulta;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.contexto.ContextoAtivacao;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

public class IFGarantidor extends BaseGarantias implements IIFGarantidor {


public void incluirIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception {
      HabilitaGarantidorDO dao = new HabilitaGarantidorDO();
      InstrumentoFinanceiroDO ifDO = InstrumentoFinanceiroFactory.getInstance().obterInstrumentoFinanceiro(codigoIF);
      
      dao.setInstrumentoFinanceiro(ifDO);
      dao.setDataInclusao(getDataHoraHoje());
      dao.setIndAtivo(Booleano.VERDADEIRO);
      dao.setDestinoGarantia(new NomeAbreviado("AGRO"));
      dao.setEntidadeAtualiza(obterUsuarioDO());

      getGp().saveOrUpdate(dao);
   }
   
   public void habilitarIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception {
	    HabilitaGarantidorDO dao = new HabilitaGarantidorDO();	    
	    dao = obtemIFGarantidor(codTipoIF, codigoIF);   		 
	    
	    dao.setDataAlteracao(getDataHoraHoje());
	    dao.setIndAtivo(Booleano.VERDADEIRO);
	    dao.setEntidadeAtualiza(obterUsuarioDO());

	    getGp().saveOrUpdate(dao);
	}
   
   public void desabilitarIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception {
	    HabilitaGarantidorDO dao = new HabilitaGarantidorDO();	    
	    dao = obtemIFGarantidor(codTipoIF, codigoIF);   		 
	    
	    dao.setDataAlteracao(getDataHoraHoje());
	    dao.setIndAtivo(Booleano.FALSO);
	    dao.setEntidadeAtualiza(obterUsuarioDO());

	    getGp().saveOrUpdate(dao);
	}

   public Booleano existeGarantidorHabilitado(CodigoTipoIF codTipoIF, CodigoIF codIF)throws Exception {
      Booleano res = Booleano.FALSO;

      List paramIFGarantidor = getGp().find(
            " select count(*) from HabilitaGarantidorDO h " +
            " where h.instrumentoFinanceiro.codigoIF = ? " + 
         // "   and h.instrumentoFinanceiro.tipoIF.codigoTipoIF = ? " + 
            "   and h.dataExclusao is null " ,
         // new Object[] { codIF, codTipoIF });
            new Object[] { codIF });
      

      Integer count = (Integer) paramIFGarantidor.get(0);
      if (count.intValue() > 0) {
         res = Booleano.VERDADEIRO;
      }

      return res;
   }

   public HabilitaGarantidorDO obtemIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codigoIF) throws Exception{
	   HabilitaGarantidorDO dao = null;

      List lParamIF = getGp().find(
            " from " + HabilitaGarantidorDO.class.getName() +  " h " + 
            " where h.instrumentoFinanceiro.codigoIF = ? " + 
            "   and h.instrumentoFinanceiro.tipoIF.codigoTipoIF = ? " + 
            "   and h.dataExclusao is null " +
            " order by h.dataInclusao desc " ,
             new Object[] { codigoIF, codTipoIF });

      if (!Condicional.vazio(lParamIF)) {
    	  dao = (HabilitaGarantidorDO) lParamIF.get(0);
      }

      return dao;
   }

   public void excluirIFGarantidor(CodigoTipoIF codTipoIF, CodigoIF codigoIF) throws Exception{
	   HabilitaGarantidorDO dao = new HabilitaGarantidorDO();	
	   dao = obtemIFGarantidor(codTipoIF, codigoIF);  
	   
	   dao.setDataExclusao(getDataHoraHoje());
	   dao.setEntidadeAtualiza(obterUsuarioDO());
            
       getGp().saveOrUpdate(dao);
   }
   
   public List obtemListaIFGarantidorHabilitado(CodigoTipoIF codTipoIF, CodigoIF codigoIF) throws Exception {
	   
	   StringBuffer sql = new StringBuffer();
	   
       sql.append(" from ");	        
       sql.append(HabilitaGarantidorDO.class.getName()).append(" h ");
       
	   if ( !Condicional.vazio(codTipoIF) || !Condicional.vazio(codigoIF) ) {
    	   
		   sql.append(" where ");
       
		   if (!Condicional.vazio(codTipoIF)){
			   sql.append(" h.instrumentoFinanceiro.tipoIF.codigoTipoIF = :codigoTipoIF ");
		    // sql.append("   and h.dataExclusao is null");
		   }
       
		   if (!Condicional.vazio(codigoIF)){
			   if (!Condicional.vazio(codTipoIF)){
				   sql.append(" and ");   
			   }
			   sql.append(" h.instrumentoFinanceiro.codigoIF = :codigoIF ");
		   }
	   }
	   sql.append(" order by h.instrumentoFinanceiro.codigoIF, h.dataInclusao desc ");
       
       IConsulta consulta = getGp().criarConsulta(sql.toString());

       if (!Condicional.vazio(codTipoIF)){
    	   consulta.setAtributo("codigoTipoIF", codTipoIF);
       }
       
       if (!Condicional.vazio(codigoIF)){
    	   consulta.setAtributo("codigoIF", codigoIF); 
       }

	   return consulta.list();
   }
   
   private UsuarioDO obterUsuarioDO() throws Exception {
	 ContextoAtivacaoVO contextoAtivacaoVO = ContextoAtivacao.getContexto();
	 IGerenciadorPersistencia gp = GerenciadorPersistenciaFactory.getGerenciadorPersistencia();		
	 return  (UsuarioDO) gp.load(UsuarioDO.class, new Id(contextoAtivacaoVO.getIdUsuario().toString()));
   }
   
   public Booleano podeExcluirIFGarantidorHabilitado(CodigoTipoIF codTipoIF, CodigoIF codigoIF)throws Exception {
	   Booleano res = Booleano.FALSO;
	   
	   StringBuffer sql = new StringBuffer();

       sql.append(" from ");	        
       sql.append(HabilitaGarantidorDO.class.getName()).append(" hg , ");
       sql.append(DetalheGarantiaDO.class.getName()).append(" dg ");
       sql.append(" where hg.instrumentoFinanceiro.id = dg.instrumentoFinanceiro.id " );       
       sql.append("   and hg.instrumentoFinanceiro.codigoIF = :codigoIF ");
       sql.append("   and dg.cestaGarantias.statusCesta.numIdStatusCesta = 14 "); //14 - Status (Vinculado)
       sql.append("   and dg.quantidadeGarantia > 0 ");
       
       IConsulta consulta = getGp().criarConsulta(sql.toString());
       consulta.setAtributo("codigoIF", codigoIF);
      
       if (consulta.list().isEmpty()) {
          res = Booleano.VERDADEIRO;
       }
       
       return res;
	   
   }
   
   public CodigoTipoIF obterComboTipoIF()  throws Exception {
	   TipoIFDO tipoIFDO = null;
       CodigoTipoIF codigoTipoIF = new CodigoTipoIF();
       StringBuffer hql = new StringBuffer();

        hql.append("from " + TipoIFDO.class.getName() + " ti " ); 
        hql.append("where ti.objetoServico.indPlatBaixa = 'S' " ); 
    	hql.append("  and ti.objetoServico.indCompoePerfil = 'S' " ); 
    	hql.append("  and ti.codigoTipoIF <> 'SWAP' " );
    	hql.append("order by ti.codigoTipoIF" );

       IConsulta consulta = GerenciadorPersistenciaFactory.getGerenciadorPersistencia().criarConsulta(hql.toString());
       Iterator iteratorTiposIF = consulta.list().iterator();

       codigoTipoIF.getDomain().add(new CodigoTipoIF(""));

       while (iteratorTiposIF.hasNext()) {
           tipoIFDO = (TipoIFDO) iteratorTiposIF.next();
           codigoTipoIF.getDomain().add(new CodigoTipoIF(tipoIFDO.getCodigoTipoIF().obterConteudo()));
       }
       
       return codigoTipoIF;
       
	}
}

package br.com.cetip.aplicacao.garantias.negocio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.cetip.aplicacao.garantias.apinegocio.FiltroCestaBean;
import br.com.cetip.aplicacao.garantias.apinegocio.IFiltroCestas;
import br.com.cetip.aplicacao.sap.apinegocio.ContaParticipanteFactory;
import br.com.cetip.dados.aplicacao.garantias.CestaGarantiasDO;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Booleano;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NumeroCestaGarantia;
import br.com.cetip.infra.atributo.tipo.texto.Texto;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.persistencia.IGerenciadorPersistencia;
import br.com.cetip.infra.servico.contexto.ContextoAtivacaoVO;

class FiltroCestas extends BaseGarantias implements IFiltroCestas {

   /**
    * Consulta cestas que atendam ao filtro informado
    * 
    * @param numero
    * @param garantidor
    * @param garantido
    * @param status
    * @param tipoAcesso
    *           filtra o acesso pelo nivel de permissao do Contexto de Ativacao 1 = Garantido; 2 = Garantidor; 0 = Ambos
    * @param reset
    * @return
    */
   private List filtrarCestasPor(FiltroCestaBean valores, int tipoAcesso) {
      NumeroCestaGarantia numero = valores.numero;
      CodigoContaCetip garantidor = valores.garantidor;
      CodigoContaCetip garantido = valores.garantido;
      Booleano somenteComAtivoInadimplente = valores.somenteComAtivoInadimplente;
      Booleano somenteComEmissorInadimplente = valores.somenteComEmissorInadimplente;
      Id status = valores.status;
      Texto reset = valores.reset;
      CodigoTipoIF tipoIF = valores.tipoIF;

      IGerenciadorPersistencia gp = getGp();
      ContextoAtivacaoVO ca = getContextoAtivacao();

      boolean filtraPermissaoGarantido = false;
      boolean filtraPermissaoGarantidor = false;

      boolean filtraGarantido = !Condicional.vazio(garantido);
      boolean filtraGarantidor = !Condicional.vazio(garantidor);
      boolean filtraPorAtivoGarantido = !Condicional.vazio(tipoIF);

      if (!ca.ehCETIP()) {
         filtraPermissaoGarantido = tipoAcesso == 1 || tipoAcesso == 0;
         filtraPermissaoGarantidor = tipoAcesso == 2 || tipoAcesso == 0;
      }

      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "filtraPermissaoGarantido: " + filtraPermissaoGarantido);
         Logger.debug(this, "filtraPermissaoGarantidor: " + filtraPermissaoGarantidor);
         Logger.debug(this, "filtraGarantido: " + filtraGarantido);
         Logger.debug(this, "filtraGarantidor: " + filtraGarantidor);
      }

      List hqlValues = new ArrayList();

      StringBuffer hql = new StringBuffer(5000);
      hql.append("select c,");
      hql.append("sum(case when ifAtivo.indInadimplencia = 'S' then 1 else 0 end) as totalAtivosInadimplentes, ");
      hql.append("sum(case when emissor.indInadimplencia = 'S' then 1 else 0 end) as totalEmissoresInadimplentes ");
      hql.append(" from CestaGarantiasDO c ");
      hql.append(" left join c.detalhes dg2 ");
      hql.append(" left join dg2.instrumentoFinanceiro ifAtivo ");
      hql.append(" left join ifAtivo.contaParticipante emissor ");

      if (filtraPorAtivoGarantido) {
         hql.append(" inner ");
      } else {
         hql.append(" left ");
      }

      hql.append(" join c.ativosVinculados ativosVinc ");

      if (!filtraPermissaoGarantidor && filtraPermissaoGarantido) {
         hql.append(" join c.visualizadores g join g.contaParticipante garantido");
      } else if (filtraPermissaoGarantidor && filtraPermissaoGarantido) {
         hql.append(" left join c.visualizadores g left join g.contaParticipante garantido");
      }

      hql.append(" where 1 = 1 ");

      if (!Condicional.vazio(reset)) {
         hql.append(" and c.indRegraLiberacao = ?");
         hqlValues.add(reset);
      }

      if (filtraPermissaoGarantido || filtraPermissaoGarantidor) {
         hql.append(" and (");

         Id idParticipante = new Id(ca.getIdParticipante().toString());
         Id idUsuario = new Id(ca.getIdUsuario().toString());

         String controleAcesso;
         try {
            controleAcesso = ContaParticipanteFactory.getInstance().obterSelectCodContasFamiliaPorCodAcesso(null,
                  idParticipante, idUsuario);
         } catch (Exception e) {
            throw new Erro(CodigoErro.ERRO, "SAP: " + e.getMessage());
         }

         // Filtros por tipo
         if (filtraPermissaoGarantido) {
            // Garantido
            StringBuffer filtroGarantido = new StringBuffer(500);
            filtroGarantido.append(" (garantido.codContaParticipante in ( ");
            filtroGarantido.append(controleAcesso);
            filtroGarantido.append(" )");
            filtroGarantido.append(" and c.statusCesta.numIdStatusCesta not in (3,4)) ");

            hql.append(filtroGarantido);
         }

         if (filtraPermissaoGarantido && filtraPermissaoGarantidor) {
            hql.append(" or ");
         }

         if (filtraPermissaoGarantidor) {
            // Garantidor
            StringBuffer filtroGarantidor = new StringBuffer(500);
            filtroGarantidor.append(" c.garantidor.codContaParticipante in ( ");
            filtroGarantidor.append(controleAcesso);
            filtroGarantidor.append(" )");

            hql.append(filtroGarantidor);
         }

         hql.append(") ");
      }

      if (!Condicional.vazio(numero)) {
         hql.append(" and c.numIdCestaGarantias = ?");
         hqlValues.add(new Id(numero.toString()));
      }

      if (filtraGarantidor) {
         hql.append(" and c.garantidor.codContaParticipante = ?");
         hqlValues.add(garantidor);
      }

      if (filtraGarantido) {
         hql.append(" and c.garantido.codContaParticipante = ?");
         hqlValues.add(garantido);
      }

      if (!Condicional.vazio(status)) {
         hql.append(" and c.statusCesta.numIdStatusCesta = ?");
         hqlValues.add(status);
      }

      if (!Condicional.vazio(tipoIF)) {
         hql.append(" and ativosVinc.instrumentoFinanceiro.tipoIF.codigoTipoIF = ?");
         hqlValues.add(tipoIF);
      }

      hql.append(" group by c.numIdCestaGarantias, ");
      hql.append(" c.indRegraLiberacao, ");
      hql.append(" c.datAlteracaoStatusCesta, ");
      hql.append(" c.datCriacao, ");
      hql.append(" c.datExclusao, ");
      hql.append(" c.datExecucao, ");
      hql.append(" c.datFechamento, ");
      hql.append(" c.datInadimplencia, ");
      hql.append(" c.datReabertura, ");
      hql.append(" c.garantidor, ");
      hql.append(" c.indInadimplencia, ");
      hql.append(" c.numPrazoExpiracao, ");
      hql.append(" c.statusCesta, ");
      hql.append(" c.indSegundoNivel, ");
      hql.append(" c.tipoGarantia, ");
      hql.append(" c.parametroPonta,");
      hql.append(" c.garantido,");
      hql.append(" c.conta60Garantido");

      if (!Condicional.vazio(somenteComEmissorInadimplente) || !Condicional.vazio(somenteComAtivoInadimplente)) {
         hql.append(" having ");

         boolean previous = false;
         if (!Condicional.vazio(somenteComEmissorInadimplente)) {
            previous = true;
            if (somenteComEmissorInadimplente.ehVerdadeiro()) {
               hql.append("sum(case when emissor.indInadimplencia = 'S' then 1 else 0 end) > 0 ");
            } else {
               hql.append("sum(case when emissor.indInadimplencia = 'S' then 1 else 0 end) = 0 ");
            }
         }

         if (!Condicional.vazio(somenteComAtivoInadimplente)) {
            if (previous) {
               hql.append(" and ");
            }

            if (somenteComAtivoInadimplente.ehVerdadeiro()) {
               hql.append("sum(case when ifAtivo.indInadimplencia = 'S' then 1 else 0 end) > 0 ");
            } else {
               hql.append("sum(case when ifAtivo.indInadimplencia = 'S' then 1 else 0 end) = 0 ");
            }
         }
      }

      hql.append(" order by c.numIdCestaGarantias desc ");

      String _hql = hql.toString();
      if (Logger.estaHabilitadoDebug(this)) {
         Logger.debug(this, "Filtrando Cestas pelo HQL: " + _hql);
      }

      List resultado = gp.find(_hql, hqlValues.toArray());
      List retorno = new ArrayList(resultado.size());
      Iterator it = resultado.iterator();
      while (it.hasNext()) {
         Object[] linha = (Object[]) it.next();
         CestaGarantiasDO cesta = (CestaGarantiasDO) linha[0];
         retorno.add(cesta);

         int l1 = ((Integer) linha[1]).intValue();
         cesta.setIndInadimplenciaAtivo(l1 > 0 ? Booleano.VERDADEIRO : Booleano.FALSO);

         int l2 = ((Integer) linha[2]).intValue();
         cesta.setIndInadimplenciaEmissor(l2 > 0 ? Booleano.VERDADEIRO : Booleano.FALSO);
      }

      return retorno;
   }

   public List filtrarCestasGarantidoPor(FiltroCestaBean valores) {
      return filtrarCestasPor(valores, 1);
   }

   public List filtrarCestasGarantidorPor(FiltroCestaBean valores) {
      return filtrarCestasPor(valores, 2);
   }

   public List filtrarCestasPorAmbos(FiltroCestaBean valores) {
      return filtrarCestasPor(valores, 0);
   }

}
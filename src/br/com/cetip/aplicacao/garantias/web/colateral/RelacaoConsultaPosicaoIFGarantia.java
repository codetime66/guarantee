package br.com.cetip.aplicacao.garantias.web.colateral;

import br.com.cetip.aplicacao.garantias.servico.colateral.RequisicaoServicoConsultaPosicaoIFGarantia;
import br.com.cetip.base.web.acao.Relacao;
import br.com.cetip.base.web.acao.suporte.Links;
import br.com.cetip.base.web.acao.suporte.Servicos;
import br.com.cetip.base.web.layout.manager.grupo.Grupo;
import br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos;
import br.com.cetip.infra.atributo.Atributo;
import br.com.cetip.infra.atributo.CodigoErro;
import br.com.cetip.infra.atributo.Contexto;
import br.com.cetip.infra.atributo.Erro;
import br.com.cetip.infra.atributo.tipo.expressao.Natureza;
import br.com.cetip.infra.atributo.tipo.identificador.CPFOuCNPJ;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoContaCetip;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoIF;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoSistema;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoConta;
import br.com.cetip.infra.atributo.tipo.identificador.CodigoTipoIF;
import br.com.cetip.infra.atributo.tipo.identificador.Id;
import br.com.cetip.infra.atributo.tipo.identificador.NomeSimplificado;
import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;
import br.com.cetip.infra.atributo.tipo.numero.QuantidadeInteiraPositiva;
import br.com.cetip.infra.atributo.tipo.numero.ValorMonetario;
import br.com.cetip.infra.atributo.tipo.tempo.Data;
import br.com.cetip.infra.atributo.tipo.texto.RazaoSocial;
import br.com.cetip.infra.atributo.utilitario.Condicional;
import br.com.cetip.infra.log.Logger;
import br.com.cetip.infra.servico.interfaces.Requisicao;


public class RelacaoConsultaPosicaoIFGarantia extends Relacao {


	public RelacaoConsultaPosicaoIFGarantia() {
		super(30);
	}
	

/*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarColunas(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarColunas(GrupoDeAtributos atributos, Grupo dados, Servicos servicos) throws Exception {	  
	   atributos.grupoDeColunas(new CodigoContaCetip(Contexto.CONTA_GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new RazaoSocial(Contexto.GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new CodigoTipoConta(Contexto.CONTA_GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new Natureza(Contexto.NATUREZA_GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new CodigoSistema(Contexto.GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new CodigoTipoIF(Contexto.TIPO_IF_GARANTIDO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new CodigoIF(Contexto.CODIGO_IF_GARANTIDO), new Id(Contexto.GARANTIDO));   
	   atributos.grupoDeColunas(new Data(Contexto.GARANTIDO_DATA_VENCIMENTO), new Id(Contexto.GARANTIDO));
	   atributos.grupoDeColunas(new CodigoSistema(Contexto.GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new CodigoTipoIF(Contexto.TIPO_IF_GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new CodigoIF(Contexto.CODIGO_IF_GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new QuantidadeInteiraPositiva(Contexto.GARANTIDOR_QUANTIDADE), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new ValorMonetario(Contexto.GARANTIDOR_VAL_CURVA), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new Data(Contexto.GARANTIDOR_DATA_ATUALIZACAO), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new ValorMonetario(Contexto.GARANTIDOR_VAL_MTM), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new Data(Contexto.GARANTIDOR_DATA_ATU_MTM), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new Id(Contexto.GARANTIDOR_CESTA_GARANTIAS), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new CodigoContaCetip(Contexto.CONTA_GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new RazaoSocial(Contexto.GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new CodigoTipoConta(Contexto.CONTA_GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new Natureza(Contexto.NATUREZA_GARANTIDOR), new Id(Contexto.GARANTIDOR));
	   atributos.grupoDeColunas(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDOR), new Id(Contexto.GARANTIDOR));
   }

/*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#chamarServico(br.com.cetip.base.web.layout.manager.grupo.Grupo,
    * br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public GrupoDeAtributos chamarServico(Grupo dados, Servicos servicos) throws Exception {

	   return servicos.chamarServico(new RequisicaoServicoConsultaPosicaoIFGarantia(), dados);
      }


   /*
    * (non-Javadoc)
    *
    * @see
    * br.com.cetip.base.web.acao.Relacao#informarParametros(br.com.cetip.base.web.layout.manager.grupo.GrupoDeAtributos,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarParametros(GrupoDeAtributos parametros, Grupo dados, Servicos servicos) throws Exception {
	   parametros.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDO));
	   parametros.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDO));
	   parametros.atributo(new Natureza(Contexto.NATUREZA_GARANTIDO));
	   parametros.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDO));
	   parametros.atributo(new CodigoContaCetip(Contexto.CONTA_GARANTIDOR));
	   parametros.atributo(new NomeSimplificado(Contexto.NOME_SIMPLIFICADO_GARANTIDOR));
	   parametros.atributo(new Natureza(Contexto.NATUREZA_GARANTIDOR));
	   parametros.atributo(new CPFOuCNPJ(Contexto.CPF_CNPJ_GARANTIDOR));
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#obterDestino(br.com.cetip.infra.atributo.Atributo,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public Class obterDestino(Atributo atributo, Grupo dados, Servicos servicos) throws Exception {
	   return null;
	     
   }

   /*
    * (non-Javadoc)
    *
    * @see br.com.cetip.base.web.acao.Relacao#informarLinks(br.com.cetip.base.web.acao.suporte.Links,
    * br.com.cetip.base.web.layout.manager.grupo.Grupo, br.com.cetip.base.web.acao.suporte.Servicos)
    */
   public void informarLinks(Links links, Grupo linha, Servicos servicos) throws Exception {
	   
   }
	
	public boolean retornarPorArquivo(Grupo grupo, Servicos servicos) {
		ConsultaPosicaoIFGarantia filtro;
		
		try {
			filtro = (ConsultaPosicaoIFGarantia)obterTela(ConsultaPosicaoIFGarantia.class);
		}
		catch (Exception e) {
			Logger.info(this, e);
			throw new Erro(CodigoErro.ERRO);
		}
		
		NumeroInteiro qtdeLinhas = filtro.getQtdeLinhas();
		return (qtdeLinhas.obterInt() > 5000);
	}
	
	 public String obterNomeDoArquivo() {
		return "MMG-CONSULTA-POSICAOIF";
	 }
		 
	 public Requisicao obterRequisicaoParaRetornoEmArquivo(Grupo grupo, Servicos servicos) {
		RequisicaoServicoConsultaPosicaoIFGarantia requisicao = new RequisicaoServicoConsultaPosicaoIFGarantia();
		CodigoContaCetip codConta = (CodigoContaCetip)grupo.obterAtributo(CodigoContaCetip.class, Contexto.MALOTE);
		requisicao.atribuirMALOTE_CodigoContaCetip(codConta); // obrigatorio
		Data data = (Data)grupo.obterAtributo(Data.class, Contexto.CONSULTA);
		
		if (!Condicional.vazio(data)){
			requisicao.atribuirCONSULTA_Data(data);
	    }
		
		return requisicao;
	 }
	
	

}
package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.Collection;

import br.com.cetip.infra.atributo.tipo.identificador.Funcao;

public interface IMapaAcoes {

   public boolean ehAcaoUnica(Funcao acao);

   public Collection obterAcoesPara(Funcao tipoAcesso);

   public boolean ehAcaoConsulta(Funcao acao);

}

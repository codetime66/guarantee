package br.com.cetip.aplicacao.garantias.negocio.agro;

import br.com.cetip.infra.atributo.tipo.numero.NumeroInteiro;

public class Parametros {
	
   private Parametros(){}
   
   public static final NumeroInteiro INCLUIR = new NumeroInteiro(0);
   public static final NumeroInteiro ATIVAR = new NumeroInteiro(1);   
   public static final NumeroInteiro DESATIVAR = new NumeroInteiro(2);
   public static final NumeroInteiro EXCLUIR = new NumeroInteiro(3);
   }

package br.com.cetip.aplicacao.garantias.apinegocio;

import java.util.List;

public interface IFiltroCestas {

   public List filtrarCestasGarantidoPor(FiltroCestaBean valores);

   public List filtrarCestasGarantidorPor(FiltroCestaBean valores);

   public List filtrarCestasPorAmbos(FiltroCestaBean valores);

}

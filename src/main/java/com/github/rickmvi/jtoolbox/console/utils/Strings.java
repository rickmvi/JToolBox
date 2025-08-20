package com.github.rickmvi.jtoolbox.console.utils;

public class Strings {


}

/*
 *  REGEX USO:
 *  0-9 -> aceita numeral de 0 a 9
 *  a-z -> aceita alfa numericos de 'a' a 'z'
 *  A-Z -> aceita alfa numericos em caixa alta/uppercase de A a Z - exp: "^[a-zA-Z]+$"
 *  ^ -> "inicio" delimita que tem de iniciar com x caracter ou negação - exp: "^[0-9]" | "^[^0-9]+$" -> 1hello 2world 3! = true
 *  $ -> "final" diz onde terminal e com qual caracter termina - exp: "^[0-9]$" -> 1 2 3 4 5 6 = true
 *  + -> indica que aceita naquele regex, um ou mais caraters delimitados - exp: "^[0-9]+$" -> 999 231345 = true
 *  {n} -> delimita o número de caracters aceitos para validação - exp: "^[0-9]{4}$" | n = '4' quantidade de numeros -> 3321 = true
 *  , -> aceita de um caracter em diante ou até - exp: "^[0-9]{4,}$" ou "^[0-9]{4,8}$" -> 3454534 - 2211
 *  . -> aceita qualquer coisa
 *  ? -> opicional
 */
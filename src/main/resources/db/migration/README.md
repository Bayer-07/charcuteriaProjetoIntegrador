CUDIDADO COM ESSA PASTA

Ela serve para controlar o banco, tudo que é feito aqui NAO PODE ser desfeito, nunca mexa em uma migration ja criada, sempre crie uma nova se precisar.

A nomenclatura dos arquivos TEM QUE SER, não é opcinal:
{numero da versao, ex: V1, V2, V3}__(duas underline)nome_do_arquivo.sql
EX: V1__criacao_da_table_cliente.sql

Toda vez que inicia o programa ele lê isso aqui e aplica as migrations que nao foram aplicadas ainda, por isso nao pode mudar alguma que ja existia.

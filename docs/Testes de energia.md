# Avaliação do consumo elétrico

## Método

A avaliação considerou 30 minutos de operação do micromouse, alimentado por uma bateria LiPo 2S de 800 mAh. Foram utilizados dados simulados de corrente, com registros a cada 10 segundos, totalizando 181 amostras.

O código preparado para o ensaio físico mantém os dois motores acionados com PWM fixo e realiza a leitura do INA219 a cada segundo. O ESP32 utiliza `millis()` para controlar o tempo e grava a corrente e o instante da leitura em um arquivo CSV na memória interna.

O consumo acumulado foi calculado em Python pela integração da corrente pelo método dos trapézios.

## Resultados

A corrente média foi de 937,3 mA. Ao final dos 30 minutos, o consumo calculado foi de 468,7 mAh, equivalente a 58,6% da capacidade nominal da bateria.

| Parâmetro | Resultado |
| :--- | ---: |
| Duração | 30 min |
| Corrente média | 937,3 mA |
| Consumo acumulado | 468,7 mAh |
| Capacidade nominal da bateria | 800 mAh |
| Parcela consumida | 58,6% |

## Conclusão

Para a corrente adotada, o consumo em 30 minutos ficou abaixo da capacidade nominal da bateria de 800 mAh. O resultado indica que essa capacidade é suficiente para o cenário considerado. A confirmação da autonomia do robô depende da medição em funcionamento, pois a carga dos motores pode ser maior no contexto do labirinto.

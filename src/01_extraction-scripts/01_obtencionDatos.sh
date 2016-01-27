mkdir ../../data/resource
mkdir ../../data/resource/DatosDescarga
#Para obtener la cookie a incluir, se debe ejecutar desde, por ejemplo, el Advanced REST Client de Chrome, la  operación GET sobre la siguiente URL: http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/03/030018TC&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico

#for linea in `cat InformesEstadisticaLocal-URLs.csv`; do
for linea in `sort -u ../00_select-reports/InformesEstadisticaLocal-URLs.csv | cat`; do  #eliminamos duplicados
  IFS=',' read -ra valores <<< "$linea"
  f=../../data/resource/DatosDescarga/${valores[1]}.csv
  curl -v -L -A "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" --cookie "sawU=granpublico; ORA_BIPS_LBINFO=1527b14c36a; ORA_BIPS_NQID=1nk2ul0785k0icsgdstvb5oioc2jeuglm0ulkvn5o4a8307bzOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1452781837.1453735893.10; __utmc=263932892; __utmz=263932892.1444514259.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)" "http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/${valores[0]}&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico" -o $f
  sleep 10
done

#segunda pasada para comprobar cuáles han dado algún tipo de error
for linea in `sort -u ../00_select-reports/InformesEstadisticaLocal-URLs.csv | cat`; do  #eliminamos duplicados
  IFS=',' read -ra valores <<< "$linea"
  f=../../data/resource/DatosDescarga/${valores[1]}.csv
  if grep -q "<!DOCTYPE HTML" $f; then
    curl -v -L -A "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" --cookie "sawU=granpublico; ORA_BIPS_LBINFO=1527b14c36a; ORA_BIPS_NQID=1nk2ul0785k0icsgdstvb5oioc2jeuglm0ulkvn5o4a8307bzOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1452781837.1453735893.10; __utmc=263932892; __utmz=263932892.1444514259.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)" "http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/${valores[0]}&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico" -o $f
  fi
  if grep -q "<HTML>" $f; then
    curl -v -L -A "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" --cookie "sawU=granpublico; ORA_BIPS_LBINFO=1527b14c36a; ORA_BIPS_NQID=1nk2ul0785k0icsgdstvb5oioc2jeuglm0ulkvn5o4a8307bzOr07UFe9WiFvM3; __utma=263932892.849551431.1443517596.1452781837.1453735893.10; __utmc=263932892; __utmz=263932892.1444514259.3.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)" "http://bi.aragon.es/analytics/saw.dll?Go&path=/shared/IAEST-PUBLICA/Estadistica%20Local/${valores[0]}&Action=Download&Options=df&NQUser=granpublico&NQPassword=granpublico" -o $f
  fi
done

#nota: aún puede quedar algún fichero erróneo
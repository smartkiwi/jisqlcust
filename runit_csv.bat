set PATH=%PATH%;"C:\Program Files (x86)\Java\jdk1.6.0_22\bin\"
java -classpath lib/jisqlcust.jar;lib/jopt-simple-3.2.jar;lib/javacsv.jar;lib/oracle.jar;C:/Oracle/product/10.1.0/Client_1/jdbc/lib/ojdbc14.jar com.xigole.util.sql.Jisql -user sd -password sd -driver oraclethin -cstring jdbc:oracle:thin:@osprey.ise.telcordia.com:1521:sd -formatter csv -colnames -outfile outfile.txt -c ;

#Si hay una instancia de la aplicacion ejecutandose no se inicia
proceso=$(ps -ef | grep drManhattanProfesor.jar | grep -v grep | awk '{print $2}')
java -jar /usr/bin/drManhattanProfesor.jar $proceso

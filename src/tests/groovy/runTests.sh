export CLASSPATH=.:../../../src/main/groovy:../../../target/node.x.jar:../../main/resources/jars/netty.jar:../../main/resources/jars/high-scale-lib.jar:../../../target/tests/classes

groovy MillisecondConverterTest.groovy
groovy NetTest.groovy
groovy NodexTest.groovy

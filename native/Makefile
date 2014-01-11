JAVA_HOME = /usr/lib/jvm/java-7-openjdk
CC = gcc
INCLUDE = -I$(JAVA_HOME)/include/ -I$(JAVA_HOME)/include/linux/ 
LINK    = -lX11

default:
	$(CC) -shared $(INCLUDE) $(LINK) -fPIC linuxhacks.c -o linuxhacks.so

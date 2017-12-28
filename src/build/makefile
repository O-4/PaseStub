JFLAGS = 
JC = javac
JAR = jar cfm
REMOVE = rm -rf -f
.SUFFIXES: .java .class


PACKAGE = de.upb.pasestub

SRC_DIR = ../main/java

TARGET = ../../target/dependency

TEST_DIR = ../test/java/de/upb/pasestub

TESTBIN_DIR = ../../test_bin

BIN_DIR = ../../bin

RESOURCE_DIR = ../resources

PACKAGES_SRC = $(SRC_DIR)/de/upb/pasestub

PACKAGES_TEST = $(PACKAGE).AllTestsSuite

CLASSES := $(shell find $(SRC_DIR) -type f -name '*.java')

TESTS := $(shell find $(TEST_DIR) -type f -name '*.java')

BINARY := $(shell find $(BIN_DIR) -type f -name '*.class')

BINARY_TESTS := $(shell find $(TESTBIN_DIR) -type f -name '*.class')


DEPLOY_DIR = ../../deploy

JAR_NAME = pase.jar

all:
	make compile

download:
	cd ../.. && mvn install

resolve: 
	@echo "Downloading dependencies"
	# Downloads dependency using maven
	test -d $(TARGET) || make download
	cd ../.. && mvn dependency:copy-dependencies;
	# Copies jar files to resource directory. (Only overwrites if newer)
	cp -n -f $(TARGET)/*.jar $(RESOURCE_DIR);

compile:
	@echo "Compiling..."
	test -d $(BIN_DIR) || mkdir $(BIN_DIR)
	$(JC) -d $(BIN_DIR) -cp "$(RESOURCE_DIR)/*" $(JFLAGS) $(CLASSES)

test:
	@echo "Compiling classes..."
	make compile
	@echo "Compiling tests..."
	test -d $(TESTBIN_DIR) || mkdir $(TESTBIN_DIR)
	$(JC) -d $(TESTBIN_DIR) -classpath "$(RESOURCE_DIR)/*:$(BIN_DIR)" $(JFLAGS) $(TESTS)
	@echo "Testing..."
	java -classpath "$(RESOURCE_DIR)/*:$(TESTBIN_DIR):$(BIN_DIR)" org.junit.runner.JUnitCore $(PACKAGES_TEST)
	
pack:
	make clean
	@echo "Compiling classes..."
	make compile
	@echo "Packing..."
	cp -n -a -v -R $(SRC_DIR)/* $(BIN_DIR)
	test -d $(DEPLOY_DIR) || mkdir $(DEPLOY_DIR)
	$(JAR) $(DEPLOY_DIR)/$(JAR_NAME) manifest.mf -C $(BIN_DIR) .
	$(REMOVE) $(BIN_DIR)


clean:
	$(REMOVE) $(BIN_DIR)
	$(REMOVE) $(TESTBIN_DIR)
	$(REMOVE) $(DEPLOY_DIR)

JFLAGS = 
JC = javac
JAR = jar cfm
REMOVE = rm -rf -f
.SUFFIXES: .java .class

ROOT_DIR = .

SRC_DIR = $(ROOT_DIR)/src

PROJECT_SRC_DIR = $(SRC_DIR)/main/java

PACKAGE = de.upb.pasestub

TEST_DIR = $(SRC_DIR)/test/java

TESTBIN_DIR = $(ROOT_DIR)/test_bin

BIN_DIR = $(ROOT_DIR)/bin

RESOURCE_DIR_NAME = resources

RESOURCE_DIR = $(ROOT_DIR)/$(RESOURCE_DIR_NAME)

RESOURCE_TEST_DIR_NAME = resources_test

RESOURCE_TEST_DIR = $(ROOT_DIR)/$(RESOURCE_TEST_DIR_NAME)

PACKAGES_TEST = $(PACKAGE).AllTestsSuite

CLASSES := $(shell find $(PROJECT_SRC_DIR) -type f -name '*.java')

TESTS := $(shell find $(TEST_DIR) -type f -name '*.java')

DEPLOY_DIR = $(ROOT_DIR)/build

JAR_NAME = pase.jar

all:
	make resolve
	make pack

resolve: 
	@echo "Downloading dependencies"
	cd $(ROOT_DIR) && mvn -q dependency:copy-dependencies -DoutputDirectory=$(RESOURCE_DIR_NAME) -DincludeScope=compile;
	cd $(ROOT_DIR) && mvn -q dependency:copy-dependencies -DoutputDirectory=$(RESOURCE_TEST_DIR_NAME) -DincludeScope=test;

compile:
	@echo "Compiling..."
	test -d $(BIN_DIR) || mkdir $(BIN_DIR)
	$(JC) -d $(BIN_DIR) -cp "$(RESOURCE_DIR)/*" $(JFLAGS) $(CLASSES)

test:
	@echo "Compiling classes..."
	make compile
	@echo "Compiling tests..."
	test -d $(TESTBIN_DIR) || mkdir $(TESTBIN_DIR)
	$(JC) -d $(TESTBIN_DIR) -classpath "$(RESOURCE_DIR)/*:$(RESOURCE_TEST_DIR)/*:$(BIN_DIR)" $(JFLAGS) $(TESTS)
	@echo "Testing..."
	
	java -classpath "$(RESOURCE_DIR)/*:$(RESOURCE_TEST_DIR)/*:$(TESTBIN_DIR):$(BIN_DIR)" org.junit.runner.JUnitCore $(PACKAGES_TEST)
	
pack:
	$(REMOVE) $(DEPLOY_DIR)/$(JAR_NAME)
	make clean
	@echo "Compiling classes..."
	make compile
	@echo "Packing..."
	cp -n -a -v -R $(PROJECT_SRC_DIR)/* $(BIN_DIR)
	test -d $(DEPLOY_DIR) || mkdir $(DEPLOY_DIR)
	$(JAR) $(DEPLOY_DIR)/$(JAR_NAME) manifest.mf -C $(BIN_DIR) .
	$(REMOVE) $(BIN_DIR)


clean:
	$(REMOVE) $(BIN_DIR)
	$(REMOVE) $(TESTBIN_DIR)

reset:
	make clean
	$(REMOVE) $(RESOURCE_DIR)
	$(REMOVE) $(RESOURCE_TEST_DIR)
	$(REMOVE) $(DEPLOY_DIR)/$(JAR_NAME)
	cd $(ROOT_DIR) && mvn -q clean


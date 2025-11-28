.PHONY: all publish local clean test helper

default: helper

all: publish local

COMMAND = ./gradlew

helper:
	@echo "Makefile commands:"
	@echo "  make publish    - Publish the project to the remote repository"
	@echo "  make local      - Publish the project to the local Maven repository"
	@echo "  make clean      - Clean and build the project"
	@echo "  make test       - Run the tests"

publish:
	@${COMMAND} publish

local:
	@${COMMAND} publishToMavenLocal

clean:
	@${COMMAND} clean build

test:
	@${COMMAND} test

daemon:
	@${COMMAND} -q classes --no-daemon

# End of Makefile
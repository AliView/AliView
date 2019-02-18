#!/bin/bash
set -eu -o pipefail

function print_error {
	RED='\033[0;31m'
    NC='\033[0m' # No Color

    read line file <<<$(caller)

    echo -e "${RED}"
    echo "Error"
    echo "An error occurred in line $line of file $file:" >&2
    echo "---"
    echo -e "${NC}"
}
trap print_error ERR

./make_installer_linux.sh
./make_installer_windows.sh
# no need to create for OSX


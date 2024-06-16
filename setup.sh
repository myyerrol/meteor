#!/usr/bin/env bash

set -e

PATH_ROOT=${PWD}
PATH_ENV=${PATH_ROOT}/env/
PATH_IP=${PATH_ROOT}/ip/

if [ ! -d ${PATH_ENV} ]; then
    echo "Downloading env..."
    rm -rf ${PATH_ENV}
    git clone git@github.com:memdsl/blackhole.git ${PATH_ENV}
else
    echo "Updating common scripts..."
    cd ${PATH_ENV}
    git pull origin main
    cd ${PATH_ROOT}
fi

if [ ! -d ${PATH_IP} ]; then
    echo ""
    echo "Downloading ip..."
    rm -rf ${PATH_IP}
    git clone git@github.com:memdsl/aurora.git ${PATH_IP}
else
    echo ""
    echo "Updating ip..."
    cd ${PATH_IP}
    git pull origin main
    cd ${PATH_ROOT}
fi

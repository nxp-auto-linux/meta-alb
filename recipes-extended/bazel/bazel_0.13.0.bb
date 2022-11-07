# Copyright 2017 NXP
# This recipe deploys the Bazel build tool onto an Ubuntu rootfs

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
BAZEL_PACKAGE = "bazel-${PV}-dist"

SRC_URI = "https://github.com/bazelbuild/bazel/releases/download/${PV}/${BAZEL_PACKAGE}.zip"
SRC_URI[md5sum] = "64a5124025c1618b550faec64a9b6fa3"
SRC_URI[sha256sum] = "82e9035084660b9c683187618a29aa896f8b05b5f16ae4be42a80b5e5b6a7690"

S = "${WORKDIR}"
BAZEL_DEPLOY_DIR = "/opt/bazel"
FILES:${PN} = "${BAZEL_DEPLOY_DIR}"
COMPATIBLE_MACHINE = ".*ubuntu"

RDEPENDS:${PN} = "bash"

do_install() {
	OUTPUT_DIR="${D}${BAZEL_DEPLOY_DIR}"
	DEPLOY_SCRIPT="${OUTPUT_DIR}/bazel-deploy.sh"

	install -d "${OUTPUT_DIR}"
	cp "${DL_DIR}/${BAZEL_PACKAGE}.zip" "${OUTPUT_DIR}"
	cp "${THISDIR}/${BPN}/fix_java_options.patch" "${OUTPUT_DIR}"

	echo "#!/bin/bash" > ${DEPLOY_SCRIPT}
	echo "echo -n \"Unpacking the Bazel archive... \"" >> ${DEPLOY_SCRIPT}
	echo "cd \$(dirname \$(readlink -f \$0))" >> ${DEPLOY_SCRIPT}
	echo "mkdir -p ${BAZEL_PACKAGE} &> /dev/null" >> ${DEPLOY_SCRIPT}
	echo "unzip -qq -o ${BAZEL_PACKAGE}.zip -d ${BAZEL_PACKAGE} &> /dev/null" >> ${DEPLOY_SCRIPT}
	echo "echo \"Done.\"" >> ${DEPLOY_SCRIPT}
	echo "cd ${BAZEL_PACKAGE}" >> ${DEPLOY_SCRIPT}
	echo "patch -p1 -i ../fix_java_options.patch" >> ${DEPLOY_SCRIPT}
	echo "./compile.sh" >> ${DEPLOY_SCRIPT}
	echo "ln -fs \$(pwd)/output/bazel /usr/local/bin/bazel" >> ${DEPLOY_SCRIPT}

	chmod +x ${DEPLOY_SCRIPT}
}

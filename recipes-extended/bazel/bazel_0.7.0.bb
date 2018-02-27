# Copyright 2017 NXP
# This recipe deploys the Bazel build tool onto an Ubuntu rootfs

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
BAZEL_PACKAGE = "bazel-${PV}-dist"

SRC_URI = "https://github.com/bazelbuild/bazel/releases/download/${PV}/${BAZEL_PACKAGE}.zip"
SRC_URI[md5sum] = "2a27c31907993090e00887bbdf0df32f"
SRC_URI[sha256sum] = "a084a9c5d843e2343bf3f319154a48abe3d35d52feb0ad45dec427a1c4ffc416"

S = "${WORKDIR}"
BAZEL_DEPLOY_DIR = "/opt/bazel"
FILES_${PN} = "${BAZEL_DEPLOY_DIR}"
COMPATIBLE_MACHINE = ".*ubuntu"

RDEPENDS_${PN} = "bash"

do_install() {
	OUTPUT_DIR="${D}${BAZEL_DEPLOY_DIR}"
	DEPLOY_SCRIPT="${OUTPUT_DIR}/bazel-deploy.sh"

	install -d "${OUTPUT_DIR}"
	cp "${DL_DIR}/${BAZEL_PACKAGE}.zip" "${OUTPUT_DIR}"

	echo "#!/bin/bash" > ${DEPLOY_SCRIPT}
	echo "echo -n \"Unpacking the Bazel archive... \"" >> ${DEPLOY_SCRIPT}
	echo "cd \$(dirname \$(readlink -f \$0))" >> ${DEPLOY_SCRIPT}
	echo "mkdir -p ${BAZEL_PACKAGE} &> /dev/null" >> ${DEPLOY_SCRIPT}
	echo "unzip -qq -o ${BAZEL_PACKAGE}.zip -d ${BAZEL_PACKAGE} &> /dev/null" >> ${DEPLOY_SCRIPT}
	echo "echo \"Done.\"" >> ${DEPLOY_SCRIPT}
	echo "cd ${BAZEL_PACKAGE}" >> ${DEPLOY_SCRIPT}
	echo "./compile.sh" >> ${DEPLOY_SCRIPT}
	echo "ln -fs \$(pwd)/output/bazel /usr/local/bin/bazel" >> ${DEPLOY_SCRIPT}

	chmod +x ${DEPLOY_SCRIPT}
}

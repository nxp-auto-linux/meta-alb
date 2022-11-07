do_install:append () {
	# workarond sshd to start in background so that login prompt is not
	# delayed until proper entropy is retrieved and /sbin/sshd continue

	cp ${D}${sysconfdir}/init.d/sshd ${D}${sysconfdir}/ssh/sshd
	echo "#!/bin/sh" > ${D}${sysconfdir}/init.d/sshd
	echo "cmd=\"/etc/ssh/sshd \"\$@" >> ${D}${sysconfdir}/init.d/sshd
	echo "\$cmd &" >> ${D}${sysconfdir}/init.d/sshd
}



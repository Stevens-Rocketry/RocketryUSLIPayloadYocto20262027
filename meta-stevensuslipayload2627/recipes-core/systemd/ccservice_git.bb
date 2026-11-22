SUMMARY = "command and control service daemon"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://github.com/Stevens-Rocketry/USLI2027-Command-and-Control-Service.git;protocol=https;branch=initial_implementation"
#We prevent pushing to main without a PR so this is safe
SRCREV = "${AUTOREV}"
PV = "1.0+git"

DEPENDS = "systemd"

inherit systemd
SYSTEMD_SERVICE:${PN} = "commandandcontrol.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/ccservice ${D}${bindir}/ccservice

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/commandandcontrol.service ${D}${systemd_system_unitdir}/
}
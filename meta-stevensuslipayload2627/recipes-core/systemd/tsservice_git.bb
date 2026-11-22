SUMMARY = "test service daemon"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://github.com/Stevens-Rocketry/USLI2027-Test-Service.git;protocol=https;branch=main"
#We prevent pushing to main without a PR so this is safe
SRCREV = "${AUTOREV}"
PV = "1.0+git"

DEPENDS = "systemd"

inherit systemd
SYSTEMD_SERVICE:${PN} = "test.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/tsservice ${D}${bindir}/tsservice

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/test.service ${D}${systemd_system_unitdir}/
}
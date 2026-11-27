FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:rpi = " \
    file://imu-lsm6ds3trc-overlay.dts \
    file://iio.cfg \
"

do_configure:prepend() {
    install -m 0644 ${UNPACKDIR}/imu-lsm6ds3trc-overlay.dts \
        ${S}/arch/${ARCH}/boot/dts/overlays/
}
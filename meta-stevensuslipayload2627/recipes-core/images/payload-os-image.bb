SUMMARY = "The custom image for use with the Stevens USLI 2027 payload computer."

IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL} packagegroup-rpi-test procps coreutils util-linux tsservice"

LICENSE = "MIT"

inherit core-image

COMPATIBLE_MACHINE = "^rpi$"

# Set Rootfs Size (about one gig)
IMAGE_ROOTFS_SIZE = "1048576"
IMAGE_OVERHEAD_FACTOR ?= "1.0"

# No root pass through serial
EXTRA_IMAGE_FEATURES += "empty-root-password allow-empty-password allow-root-login serial-autologin-root"

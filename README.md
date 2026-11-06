# Stevens USLI Payload OS

Yocto 6.0 (wrynose) image for the payload Raspberry Pi 4B.

| Component | Name |
|---|---|
| BSP layer | `meta-raspberrypi` (wrynose) |
| Project layer | `meta-stevensuslipayload2627` |
| Machine | `payload-pi4b` |
| Distro | `rocketrypayload` |
| Image | `payload-os-image` |

**Contents:** [Host setup](#1-host-setup) · [Configure](#2-configure) · [Build](#3-build) · [Flash](#4-flash) · [Layout](#5-layout)

---

## 1. Host setup

Install the build dependencies for your distro. Expect the build to use about 100 GB of disk.

### Ubuntu / Debian

```sh
sudo apt install build-essential chrpath cpio debianutils diffstat file gawk gcc git \
    iputils-ping libacl1 locales lz4 python3 python3-git python3-jinja2 python3-pexpect \
    python3-pip python3-subunit python3-venv socat texinfo unzip wget xz-utils zstd bmap-tools
sudo locale-gen en_US.UTF-8
```

### Arch

```sh
sudo pacman -S --needed base-devel git chrpath cpio diffstat file gawk inetutils \
    rpcsvc-proto lz4 python python-gitpython python-jinja python-pexpect python-subunit \
    socat texinfo unzip wget xz zstd perl
```

### Clone and install bitbake-setup

Clone the repo and install `bitbake-setup` into a virtual environment:

```sh
git clone --recurse-submodules <repo-url>
cd RocketryUSLIPayloadYocto20262027
python3 -m venv ./bitbake-setup-venv
. ./bitbake-setup-venv/bin/activate
pip install bitbake-setup
```

---

## 2. Configure

This only needs to be done once per checkout. When `bitbake-setup init` prompts you, choose `poky-wrynose` and any ARM machine such as `machine/qemuarm64`; the last two commands below replace it with ours.

```sh
bitbake-setup init
deactivate
cd bitbake-builds/<setup-name>/build
source init-build-env

REPO=$(realpath ../../..)
bitbake-layers add-layer $REPO/meta-raspberrypi
bitbake-layers add-layer $REPO/meta-stevensuslipayload2627
bitbake-config-build enable-fragment machine/payload-pi4b
bitbake-config-build enable-fragment distro/rocketrypayload
```

---

## 3. Build

Source the build environment in each new shell, then build the image:
```sh
cd bitbake-builds/<setup-name>/build
source init-build-env
bitbake payload-os-image
```

The first build takes a few hours; later builds reuse the download and sstate caches. The result is written to `tmp/deploy/images/payload-pi4b/payload-os-image-payload-pi4b.rootfs.wic.bz2`.

---

## 4. Flash

Flashing overwrites the entire target disk, so make sure you have the SD card and not your system drive. The easiest way is to list your disks, insert the card, and list them again to see which one appeared.

### Linux

The card shows up in `lsblk` as a removable disk (`RM` is 1) with the card's capacity, usually `/dev/sdb` or `/dev/mmcblk0`:
```sh
lsblk -d -o NAME,SIZE,MODEL,TRAN,RM
```

Unmount any partitions the desktop mounted automatically, then write the image to the whole disk rather than to a partition like `/dev/sdb1`:
```sh
sudo umount /dev/sdX?*
sudo bmaptool copy payload-os-image-payload-pi4b.rootfs.wic.bz2 /dev/sdX
```

If `bmaptool` isn't available, `dd` works but is slower:
```sh
bzcat payload-os-image-payload-pi4b.rootfs.wic.bz2 | sudo dd of=/dev/sdX bs=4M status=progress conv=fsync
```

### Windows

Open PowerShell as administrator and list the disks:
```powershell
Get-Disk | Format-Table Number, FriendlyName, BusType, @{n='SizeGB';e={[math]::Round($_.Size/1GB,1)}}, IsBoot, IsSystem
```

The card has the right capacity and shows `False` under both `IsBoot` and `IsSystem`. Substitute its `Number` for `N` when you write the image:
```powershell
& "C:\Program Files\7-Zip\7z.exe" x .\payload-os-image-payload-pi4b.rootfs.wic.bz2
& "C:\Program Files\Raspberry Pi Ltd\Imager\rpi-imager.exe" --cli .\payload-os-image-payload-pi4b.rootfs.wic \\.\PhysicalDriveN
```

---

## 5. Layout

Keep all project changes in `meta-stevensuslipayload2627`. If you need to change something in an upstream layer, override it with a `.bbappend` here instead of editing the upstream files.

| What | Where |
|---|---|
| Hardware config, hostname | `conf/machine/payload-pi4b.conf` |
| Distro policy | `conf/distro/rocketrypayload.conf` |
| Image contents | `recipes-core/images/payload-os-image.bb` |
| Payload software | `recipes-app/<name>/` |
| Settings for your own machine | `build/conf/local.conf` (not committed) |

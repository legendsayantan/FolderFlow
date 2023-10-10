# FolderFlow
### A simple tool to sync folders between computers, via removable drive.
#### Update folders, software or games from one pc to another, without any direct connectivity.

## Basic Steps:
In case of updating software/game, you need to know the installation folder on both devices.

### 0. Setup
1. Download the FolderFlow distribution zip.
2. Extract the zip inside your removable drive.

### 1. Create a flow configuration
1. Plug the removable drive in the device with non-updated folder.
2. Open a terminal/command prompt inside FolderFlow-v0.1.0/bin
3. Run the command `Folderflow -n <flowconfig.json> <target-installation-folder>`

This will scan the installation folder and create a new flow config, with the provided name at <flowconfig.json>

### 2. Compare with updated folder and generate patch
1. Plug the removable drive in the device with updated folder.
2. Open a terminal/command prompt inside FolderFlow-v0.1.0/bin
3. Run the command `Folderflow -c <flowconfig.json> <updated-installation-folder>`

This will scan the updated installation folder and generate an update patch.

### 3. Apply the update patch
1. Plug the removable drive in the device with non-updated folder.
2. Open a terminal/command prompt inside FolderFlow-v0.1.0/bin
3. Run the command `Folderflow -x <flowconfig.json>`

This will apply the update patch to the non updated installation folder.

**Run `Folderflow -h` for more information.**
**You can update multiple folders by creating multiple different flow configurations and repeating the steps.**
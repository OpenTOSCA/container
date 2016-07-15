*.xml files can be added here in order to extend the supported ArtifactTypes of this Management Bus plugin.

- Placeholder:
	 - Placeholder that should be replaced with InstanceData-Properties and/or Input-Parameters (InstanceData is prioritized) are specified with "{{PLACEHOLDER}}"
	 - Fixed placeholder:
	 	- "{TARGET_FILE_PATH}" => Pointing to the referenced file on the machine. e.g. "~/ServiceTemplate.csar/artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/Test_IA/files/TestIAScript.sh"
	 	- "{TARGET_FILE_FOLDER_PATH}" => Pointing to the parent folder of the referenced file on the machine. e.g. "~/ServiceTemplate.csar/artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/Test_IA/files"
	 	- "{TARGET_FILE_NAME_WITH_E}" => The name of the referenced file with extension. e.g. "TestIAScript.sh"
	 	- "{TARGET_FILE_NAME_WITHOUT_E}" => The name of the referenced file without extension.  e.g. "TestIAScript"
	 	- "{DA_NAME_PATH_MAP}" => Mapping of DAs and their paths. e.g. DAs="FlinkApp_DA,/artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/FlinkApp_DA/files/local_mae.py;"
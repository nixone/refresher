<?php
function version_comparator($a, $b)
{
	$arrayA = explode(".", $a);
	$arrayB = explode(".", $b);
	
	$c = min(count($arrayA), count($arrayB));
	
	for($i=0; $i<$c; $i++) {
		$versionA = (int)$arrayA[$i];
		$versionB = (int)$arrayB[$i];
		
		if($versionA > $versionB) {
			return -1;
		} else if($versionB > $versionA) {
			return 1;
		}
	}
	
	return 0;
}

function operation_version_list($versionDirectory)
{
	$handle = opendir($versionDirectory);
	
	$versions = array();
	
	while($file = readdir($handle))
	{
		if($file == "." || $file == "..") {
			continue;
		}
		
		$path = $versionDirectory."/".$file;
		
		if(is_file($path))
		{
			continue;
		}
		
		$versions[] = $file;
	}
	
	closedir($handle);
	
	usort($versions, "version_comparator");
	
	echo json_encode(array("all" => $versions, "latest" => count($versions) > 0 ? $versions[0] : null));
}

function operation_version($urlDirectory, $versionDirectory, $version)
{
	$cacheFile = $versionDirectory."/".$version.".json";
	/*
	if(file_exists($cacheFile)) {
		$fp = fopen($cacheFile, "r");
		$data = fread($fp, filesize($cacheFile));
		fclose($fp);
		
		echo $data;
		return;
	}*/
	
	$rootDirectory = $versionDirectory."/".$version;
	
	$result = array("size" => 0, "files" => array());
	
	operation_version_subdir($urlDirectory."/".$version, $rootDirectory, ".", $result['files'], $result['size']);
	
	$data = json_encode($result);
	
	$fp = fopen($cacheFile, "w");
	if($fp) {
		fwrite($fp, $data);
		fclose($fp);
	}
	
	echo $data;
}

function operation_version_subdir($urlDirectory, $rootDirectory, $currentDirectory, &$filesArray, &$totalBytes)
{
	$dirPath = $rootDirectory."/".$currentDirectory;
	
	$handle = opendir($dirPath);
	
	while($file = readdir($handle)) {
		if($file == "." || $file == "..") {
			continue;
		}
		
		$fileRelative = $currentDirectory."/".$file;
		$filePath = $dirPath."/".$file;
		
		if(is_dir($filePath)) {
			operation_version_subdir($urlDirectory, $rootDirectory, $fileRelative, $filesArray, $totalBytes);
		} else {
			$md5 = md5_file($filePath);
			$bytes = filesize($filePath);
			$relativePath = $fileRelative;
			$absolutePath = $urlDirectory."/".$currentDirectory."/".$file;
			
			$filesArray[] = array(
				"size" => $bytes,
				"md5" => $md5,
				"relativePath" => $relativePath,
				"absolutePath" => $absolutePath,
				"name" => $file
			);
			
			$totalBytes += $bytes;
		}
	}
}

$versionDirectory = dirname(__FILE__)."/versions";
$onlyFile = substr(__FILE__, strlen(dirname(__FILE__))+1);

$scriptUri = $_SERVER['SCRIPT_URI'];

if(!$scriptUri) {
	$scriptUri = "http://{$_SERVER['HTTP_HOST']}{$_SERVER['SCRIPT_NAME']}";
}

$onlyDirectoryUri = substr($scriptUri, 0, -strlen($onlyFile));
$versionUri = $onlyDirectoryUri."/versions";

switch($_GET['operation']) {
	case "": operation_version_list($versionDirectory); break;
	case "version": operation_version($versionUri, $versionDirectory, $_GET['version']); break;
	default: die("Unrecognized operation");
}

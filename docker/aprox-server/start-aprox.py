#!/usr/bin/python

import os
import sys
from urllib2 import urlopen
# import tarfile
import shutil
import fnmatch

# Envar for reading development binary volume mount point
APROX_DEV_VOL = '/tmp/aprox'
APROX_DEV_BINARIES_PATTERN='aprox*-launcher.tar.gz'

# Envars that can be set using -e from 'docker run' command.
APROX_URL_ENVAR = 'APROX_BINARY_URL'
APROX_ETC_URL_ENVAR = 'APROX_ETC_URL'
APROX_OPTS_ENVAR = 'APROX_OPTS'

# default aprox binary to download.
DEF_APROX_BINARY_URL = 'http://repo.maven.apache.org/maven2/org/commonjava/aprox/launch/aprox-launcher-savant/0.16.4/aprox-launcher-savant-0.16.4-launcher.tar.gz'

# locations for expanded aprox binary
APROX_DIR = '/opt/aprox'
APROX_ETC = os.path.join(APROX_DIR, 'etc/aprox')
APROX_STORAGE = os.path.join(APROX_DIR, 'var/lib/aprox/storage')
APROX_DATA = os.path.join(APROX_DIR, 'var/lib/aprox/data')
APROX_LOGS = os.path.join(APROX_DIR, 'var/log/aprox')

# locations on global fs
ETC_APROX = '/etc/aprox'
VAR_APROX = '/var/lib/aprox'
VAR_STORAGE = os.path.join(VAR_APROX, 'storage')
VAR_DATA = os.path.join(VAR_APROX, 'data')
LOGS = '/var/log/aprox'

def run(cmd, fail_message='Error running command', fail=True):
  print cmd
  ret = os.system(cmd)
  if fail and ret != 0:
    print "%s (failed with code: %s)" % (fail_message, ret)
    sys.exit(ret)

def move_and_link(src, target, replaceIfExists=False):
  srcParent = os.path.dirname(src)
  if not os.path.isdir(srcParent):
    os.mkdir(srcParent)
  
  if not os.path.isdir(target):
    os.mkdir(target)
  
  if os.path.isdir(src):
    for f in os.listdir(src):
      targetFile = os.path.join(target, f)
      srcFile = os.path.join(src, f)
      if os.path.exists(targetFile):
        if not replaceIfExists:
          continue
        
        if os.path.isdir(targetFile):
          shutil.rmtree(targetFile)
        else:
          os.remove(targetFile)
        
      if os.path.isdir(srcFile):
        shutil.copytree(srcFile, targetFile)
      else:
        shutil.copy(srcFile, targetFile)
    
    shutil.rmtree(src)
  
  os.symlink(target, src)


if not os.path.isdir(APROX_DIR):
  parentDir = os.path.dirname(APROX_DIR)
  if not os.path.isdir(parentDir):
    os.mkdir(parentDir)
    
  if os.path.isdir(APROX_DEV_VOL):
    for file in os.listdir(APROX_DEV_VOL):
      if fnmatch.fnmatch(file, APROX_DEV_BINARIES_PATTERN):
        devTarball = os.path.join(APROX_DEV_VOL, file)
        
        print "Unpacking development binary of AProx: %s" % devTarball
        run('tar -zxvf %s -C /opt' % devTarball)
        unpacked=True
        break
    
    if not unpacked:
      if not os.path.exists(os.path.join(APROX_DEV_VOL, 'bin/aprox.sh')):
        print "Development volume %s exists but doesn't appear to contain expanded AProx (can't find 'bin/aprox.sh'). Ignoring." % APROX_DEV_VOL
      else:
        print "Using expanded AProx deployment, in development volume: %s" % APROX_DEV_VOL
        shutil.copytree(APROX_DEV_VOL, APROX_DIR)
    
  else:
      aproxBinaryUrl = os.environ.get(APROX_URL_ENVAR) or DEF_APROX_BINARY_URL
      
      print 'Downloading: %s' % aproxBinaryUrl
      download = urlopen(aproxBinaryUrl)
      with open('/tmp/aprox.tar.gz', 'wb') as f:
        f.write(download.read())
      
      run('ls -alh /tmp/')
      
      run('tar -zxvf /tmp/aprox.tar.gz -C /opt')
    #  with tarfile.open('/tmp/aprox.tar.gz') as tar:
    #    tar.extractall(parentDir)
    
  # Git location supplying /opt/aprox/etc/aprox
  aproxEtcUrl = os.environ.get(APROX_ETC_URL_ENVAR)
  
  if aproxEtcUrl is not None:
    shutil.rmtree(APROX_ETC)
    run("git clone %s %s" % (aproxEtcUrl, APROX_ETC), "Failed to checkout aprox/etc from: %s" % aproxEtcUrl)
  
  move_and_link(APROX_ETC, ETC_APROX, replaceIfExists=True)
  move_and_link(APROX_STORAGE, VAR_STORAGE)
  move_and_link(APROX_DATA, VAR_DATA)
  move_and_link(APROX_LOGS, LOGS)


opts = os.environ.get(APROX_OPTS_ENVAR) or ''
run("%s %s" % (os.path.join(APROX_DIR, 'bin', 'aprox.sh'), opts), fail=False)
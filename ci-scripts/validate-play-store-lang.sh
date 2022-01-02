#!/usr/bin/env bash

# Reset
Color_Off='\033[0m'       # Text Reset

# Regular Colors
Black='\033[0;30m'        # Black
Red='\033[0;31m'          # Red
Green='\033[0;32m'        # Green
Yellow='\033[0;33m'       # Yellow
Blue='\033[0;34m'         # Blue
Purple='\033[0;35m'       # Purple
Cyan='\033[0;36m'         # Cyan
White='\033[0;37m'        # White

exitcode=0

supportedlangs=(af ar am hy-AM az-AZ eu-ES be bn-BD bg my-MM ca zh-HK zh-CN zh-TW hr cs-CZ da-DK nl-NL en-AU en-CA en-IN en-SG en-GB en-US et fil fi-FI fr-FR fr-CA gl-ES ka-GE de-DE el-GR iw-IL hi-IN hu-HU is-IS id it-IT ja-JP kn-IN km-KH ko-KR ky-KG lo-LA lv lt mk-MK ms ml-IN mr-IN mn-MN ne-NP no-NO fa pl-PL pt-BR pt-PT ro rm ru-RU sr si-LK sk sl es-419 es-ES es-US sq sw sv-SE ta-IN te-IN th tr-TR uk vi zu)

readarray -t dirs < <(find fastlane/metadata/android -mindepth 1 -maxdepth 1 -type d -printf '%P\n')

echo -e "${Green}Checking for Valid fastlane files...${Color_Off}"

for target in "${supportedlangs[@]}"; do
  for i in "${!dirs[@]}"; do
    if [[ ${dirs[i]} = $target ]]; then
      unset 'dirs[i]'
    fi
  done
done

if [[ ${#dirs[@]} -gt 0 ]]; then
  exitcode=1
  echo -e "${Red}Invalid Lang Play Store Listing found: ${#dirs[@]}${Color_Off}"
  echo -e "${Red}Invalid Lang codes:${dirs[@]}${Color_Off}"
else
  echo -e "${Green}All found lang codes are valid${Color_Off}"
fi

# check we have required files

requiredfiles=(title.txt full_description.txt short_description.txt video.txt)

for d in fastlane/metadata/android/* ; do
    [ -L "${d%/}" ] && continue
    for rfile in "${requiredfiles[@]}"; do
      if test ! -f "$d/$rfile"; then
        echo -e "${Red}$d/$rfile missing.${Color_Off}"
        exitcode=1
      fi
    done
    # check title is under 30 characters
    if test -f "$d/title.txt"; then
      if [[ $(wc -m < "$d/title.txt") -gt 30 ]]; then
        echo -e "${Red}$d/title.txt title too long.${Color_Off}"
        exitcode=1
      fi
    fi

#    if test -f "$d/video.txt"; then
#      fcontents=$(cat "$d/video.txt")
#      echo -e "$d/video.txt -> ${Blue}$fcontents${Color_Off}"
#    fi
done

exit $exitcode
#!/usr/bin/env bash

exitcode=0

supportedlangs=(af ar am hy-AM az-AZ eu-ES be bn-BD bg my-MM ca zh-HK zh-CN zh-TW hr cs-CZ da-DK nl-NL en-AU en-CA en-IN en-SG en-GB en-US et fil fi-FI fr-FR fr-CA gl-ES ka-GE de-DE el-GR iw-IL hi-IN hu-HU is-IS id it-IT ja-JP kn-IN km-KH ko-KR ky-KG lo-LA lv lt mk-MK ms ml-IN mr-IN mn-MN ne-NP no-NO fa pl-PL pt-BR pt-PT ro rm ru-RU sr si-LK sk sl es-419 es-ES es-US sq sw sv-SE ta-IN te-IN th tr-TR uk vi zu)

readarray -t dirs < <(find fastlane/metadata/android -mindepth 1 -maxdepth 1 -type d -printf '%P\n')

echo "Checking for Valid fastlane files..."

for target in "${supportedlangs[@]}"; do
  for i in "${!dirs[@]}"; do
    if [[ ${dirs[i]} = $target ]]; then
      unset 'dirs[i]'
    fi
  done
done

if [[ ${#dirs[@]} -gt 0 ]]; then
  exitcode=1
  echo "Invalid Lang Play Store Listing found: ${#dirs[@]}"
  echo "Invalid Lang codes:${dirs[@]}"
else
  echo "All found lang codes are valid"
fi

# check we have required files

requiredfiles=(title.txt full_description.txt short_description.txt video.txt)

for d in fastlane/metadata/android/* ; do
    [ -L "${d%/}" ] && continue
    for rfile in "${requiredfiles[@]}"; do
      if test ! -f "$d/$rfile"; then
        echo "$d/$rfile missing."
        exitcode=1
      fi
    done
    # check title is under 30 characters
    if test -f "$d/title.txt"; then
      if [[ $(wc -m < "$d/title.txt") -gt 30 ]]; then
        echo "$d/title.txt title too long."
        exitcode=1
      fi
    fi
done

exit $exitcode
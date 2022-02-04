# gradle version https://youtrack.jetbrains.com/issue/KT-45545#focus=Comments-27-5166414.0-0
FROM gradle:7.2-jdk16

# install OS packages
RUN apt-get --quiet update --yes

# Installing build tools
RUN apt-get update && \
  apt-get install -y \
  build-essential \
  ruby \
  jq \
  ruby-dev

# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common

# get link at bottom of https://developer.android.com/studio
ENV ANDROID_SDK_URL https://dl.google.com/android/repository/commandlinetools-linux-8092744_latest.zip
ENV ANDROID_SDK_CHECKSUM d71f75333d79c9c6ef5c39d3456c6c58c613de30e6a751ea0dbd433e8f8b9cbf
# https://developer.android.com/studio/releases/platform-tools
# higher version casues Warning: Failed to find package
ENV ANDROID_BUILD_TOOLS_VERSION 32.0.0
ENV ANDROID_SDK_ROOT /usr/local/android-sdk-linux
ENV ANDROID_VERSION 32
# ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
ENV PATH ${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/cmdline-tools/tools/bin


RUN mkdir "$ANDROID_SDK_ROOT" .android && \
    cd "$ANDROID_SDK_ROOT" && \
    curl -o sdk.zip $ANDROID_SDK_URL && \
    echo "${ANDROID_SDK_CHECKSUM}  sdk.zip" | sha256sum -c - && \
    unzip sdk.zip && \
    rm sdk.zip
RUN cd "$ANDROID_SDK_ROOT" && \
    mv cmdline-tools latest && \
    mkdir cmdline-tools && \
    mv latest cmdline-tools/.

RUN yes | ${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager --licenses
RUN $ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --update
# https://developer.android.com/studio/command-line/sdkmanager
RUN $ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager \
    "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" \
    "platforms;android-${ANDROID_VERSION}" \
    "platform-tools"

# install FastLane
COPY Gemfile.lock .
COPY Gemfile .
RUN gem update --system 3.0.8 # https://github.com/rubygems/rubygems/issues/3068
RUN gem install bundler
RUN bundle install

# at least 1.5G memory is required for the gitlab runner to succeed
#RUN echo "org.gradle.jvmargs=-Xmx1536m" >> local.properties
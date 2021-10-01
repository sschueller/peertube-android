FROM gradle:7-jdk16

# get link at bottom of https://developer.android.com/studio
ENV ANDROID_SDK_URL https://dl.google.com/android/repository/commandlinetools-linux-7583922_latest.zip
ENV ANDROID_SDK_CHECKSUM 124f2d5115eee365df6cf3228ffbca6fc3911d16f8025bebd5b1c6e2fcfa7faf
# https://developer.android.com/studio/releases/platform-tools
ENV ANDROID_BUILD_TOOLS_VERSION 29.0.6
ENV ANDROID_HOME /usr/local/android-sdk-linux
ENV ANDROID_VERSION 29
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

RUN mkdir "$ANDROID_HOME" .android && \
    cd "$ANDROID_HOME" && \
    curl -o sdk.zip $ANDROID_SDK_URL && \
    echo "${ANDROID_SDK_CHECKSUM}  sdk.zip" | sha256sum -c - && \
    unzip sdk.zip && \
    rm sdk.zip

RUN yes | ${ANDROID_HOME}/cmdline-tools/bin/sdkmanager --licenses
RUN $ANDROID_HOME/cmdline-tools/bin/sdkmanager --update
RUN $ANDROID_HOME/cmdline-tools/bin/sdkmanager "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" \
    "platforms;android-${ANDROID_VERSION}" \
    "platform-tools"

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

# install FastLane
COPY Gemfile.lock .
COPY Gemfile .
RUN gem update --system 3.0.8 # https://github.com/rubygems/rubygems/issues/3068
RUN gem install bundler
RUN bundle install

# at least 1.5G memory is required for the gitlab runner to succeed
#RUN echo "org.gradle.jvmargs=-Xmx1536m" >> local.properties
FROM gradle:6.1.1-jdk8

ENV ANDROID_SDK_URL https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip
ENV ANDROID_SDK_CHECKSUM 444e22ce8ca0f67353bda4b85175ed3731cae3ffa695ca18119cbacef1c1bea0
ENV ANDROID_BUILD_TOOLS_VERSION 29.0.3
ENV ANDROID_HOME /usr/local/android-sdk-linux
ENV ANDROID_VERSION 29
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

RUN mkdir "$ANDROID_HOME" .android && \
    cd "$ANDROID_HOME" && \
    curl -o sdk.zip $ANDROID_SDK_URL && \
    echo "${ANDROID_SDK_CHECKSUM}  sdk.zip" | sha256sum -c - && \
    unzip sdk.zip && \
    rm sdk.zip

RUN yes | ${ANDROID_HOME}/tools/bin/sdkmanager --licenses
RUN $ANDROID_HOME/tools/bin/sdkmanager --update
RUN $ANDROID_HOME/tools/bin/sdkmanager "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" \
    "platforms;android-${ANDROID_VERSION}" \
    "platform-tools"

# install OS packages
RUN apt-get --quiet update --yes
RUN apt-get --quiet install --yes ruby ruby-dev

# We use this for xxd hex->binary
RUN apt-get --quiet install --yes vim-common

# install FastLane
COPY Gemfile.lock .
COPY Gemfile .
RUN gem update --system
RUN gem install bundler
RUN bundle install

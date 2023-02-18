# Build Image: docker image build -t jekyll .
# Use Image:
#  - docker run -it -v .:/website -p 4000:4000 jekyll
#  - docker run --name jekyll -d -t -v .:/website -p 4000:4000 jekyll
FROM ruby:2
WORKDIR /website
COPY Gemfile* .
RUN bundle install
ENTRYPOINT bundle exec jekyll serve --host=0.0.0.0

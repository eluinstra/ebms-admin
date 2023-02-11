# Usage: docker run --name jekyll -d -t -v .:/website -p 4000:4000 jekyll
FROM ruby:2
ENTRYPOINT cd website; bundle install; bundle exec jekyll serve --host=0.0.0.0

name: Build
on:
  pull_request:
    branches: [ main ]
    types: [ opened, synchronize ]
  push:
    branches: [ main ]
  schedule:
    # Run a build once per week on the main branch.
    - cron: "0 0 * * 0"

jobs:
  codeql:
    name: Run CodeQL Analysis
    runs-on: ubuntu-latest
    permissions:
      actions: read
      contents: read
      security-events: write

    steps:
      - name: Checkout repository
        uses: actions/checkout@v3

      - name: Initialize CodeQL
        uses: github/codeql-action/init@v2
        with:
          languages: java

      # This can be replaced with a manual build if this begins to fail for any reason.
      - name: Autobuild
        uses: github/codeql-action/autobuild@v2

      - name: Perform CodeQL Analysis
        uses: github/codeql-action/analyze@v2

  test:
    name: Build and run tests

    steps:
      - name: Checkout repository
        uses: actions/checkout@v3
        with:
          # Needed to keep actions working correctly.
          fetch-depth: 2

      - name: Initialize JDK
        uses: actions/setup-java@v3
        with:
          distribution: 'zulu'
          java-version: '17'

      - name: Compile and run tests
        run: >-
          ./mvnw
          -B 
          -U
          -T8C
          --no-transfer-progress
          '-Dcheckstyle.skip=true'
          '-Dstyle.color=always'
          '-Dmaven.artifact.threads=50'
          clean verify
          
      - name: Publish to codecov
        continue-on-error: true
        if: always()
        run: bash <(curl -s https://codecov.io/bash)

      - name: Publish unit test results
        uses: EnricoMi/publish-unit-test-result-action@v2
        if: always()
        continue-on-error: true
        with:
          files: "**/target/**/TEST-*.xml"

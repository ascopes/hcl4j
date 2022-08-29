package io.github.ascopes.hcl4j.lexer.test;

import io.github.ascopes.hcl4j.core.inputs.CharInputStream;
import io.github.ascopes.hcl4j.core.lexer.DefaultLexerMode;
import io.github.ascopes.hcl4j.core.lexer.Lexer;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TerraformTest {

  @Test
  void testReadTerraform() throws IOException {
    var source = """
        terraform {
          required_providers {
            aws = {
              source  = "hashicorp/aws"
              version = "~> 4.16"
            }
          }

          required_version = ">= 1.2.0"
        }

        provider "aws" {
          region  = "us-west-2"
        }

        resource "aws_instance" "app_server" {
          ami           = "ami-08d70e59c07c61a3a"
          instance_type = "t2.micro"
        }
        
        resource "aws_s3_bucket" "b" {
          bucket = "s3-website-test.hashicorp.com"
          acl    = "public-read"
          policy = file("policy.json")
          
          website {
            index_document = "index.html"
            error_document = "error.html"
        
            routing_rules = <<EOF
        [{
            "Condition": {
                "KeyPrefixEquals": "docs/"
            },
            "Redirect": {
                "ReplaceKeyPrefixWith": "documents/"
            }
        }]
        EOF
          }
        }
        """.stripIndent().getBytes(StandardCharsets.UTF_8);

    try (var stream = new CharInputStream("example.tf", new ByteArrayInputStream(source))) {
      var lexer = new Lexer(stream, DefaultLexerMode::new);

      while (true) {
        var next = lexer.nextToken();

        System.out.println(next);

        if (next.type() == TokenType.END_OF_FILE) {
          break;
        }
      }
    }
  }
}

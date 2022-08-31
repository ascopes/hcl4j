/*
 * Copyright (C) 2022 Ashley Scopes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ascopes.hcl4j.lexer.test;

import io.github.ascopes.hcl4j.core.inputs.CharInputStream;
import io.github.ascopes.hcl4j.core.lexer.Lexer;
import io.github.ascopes.hcl4j.core.tokens.Token;
import io.github.ascopes.hcl4j.core.tokens.TokenType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TerraformTest {

  @Test
  void testReadTerraformOnce() throws IOException {
    var source = """
        # Terraform config block
        terraform {
          // Required terraform providers
          // go here.
          required_providers {
            aws = {
              source  = "hashicorp/aws"
              version = "~> 4.16"
            }
          }

          required_version = ">= 1.2.0"
        }

        provider "aws" {
          /* We operate in eu-west-1 on AWS
             because Ireland is cool */
          region  = "eu-west-1"
        }

        resource "aws_instance" "app_server" {
          ami           = "ami-08d70e59c07c61a3a"
          instance_type = "t3a.micro"
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

    try (var lex = Lexer.forHclConfigFile(
        new CharInputStream("example.tf", new ByteArrayInputStream(source))
    )) {
      Token next;

      do {
        next = lex.nextToken();
        System.out.println(next);
      } while (next.type() != TokenType.END_OF_FILE);
    }
  }
}

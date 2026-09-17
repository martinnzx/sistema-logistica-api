import os
import glob
import re

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Remove imports
    content = re.sub(r'^import io\.swagger\.v3\.oas\.annotations.*$\n', '', content, flags=re.MULTILINE)
    
    # We can use regex to remove annotations that span multiple lines.
    # Because nested parentheses are hard for standard regex, we use a loop for replacements
    
    # Remove @Tag(...)
    content = re.sub(r'@Tag\s*\([^)]*\)\s*', '', content)
    
    # Remove @Schema(...)
    content = re.sub(r'@Schema\s*\([^)]*\)\s*', '', content)
    
    # Remove @Parameter(...)
    content = re.sub(r'@Parameter\s*\([^)]*\)\s*', '', content)
    
    # Remove @Operation( ... ) - this might have nested parentheses from @Schema
    # Actually, a better approach is replacing anything from @Operation( up to the next Spring annotation
    content = re.sub(r'@Operation\s*\([\s\S]*?\)\s*(?=@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|@PatchMapping)', '', content)
    
    # Remove @ApiResponses( ... )
    content = re.sub(r'@ApiResponses\s*\([\s\S]*?\)\s*(?=@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|@PatchMapping)', '', content)
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

for filepath in glob.glob('src/main/java/**/*.java', recursive=True):
    process_file(filepath)

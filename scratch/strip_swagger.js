const fs = require('fs');
const path = require('path');

function processFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    const originalContent = content;

    // Remove imports
    content = content.replace(/^import io\.swagger\.v3\.oas\.annotations.*$/gm, '');

    // Clean up empty lines created by import removal
    content = content.replace(/\n\s*\n\s*\n/g, '\n\n');

    // Remove @Tag
    content = content.replace(/@Tag\s*\([^)]*\)\s*/g, '');

    // Remove @Schema
    content = content.replace(/@Schema\s*\([^)]*\)\s*/g, '');

    // Remove @Parameter
    content = content.replace(/@Parameter\s*\([^)]*\)\s*/g, '');
    
    // Remove @ArraySchema
    content = content.replace(/@ArraySchema\s*\([^)]*\)\s*/g, '');
    
    // Remove @Content
    content = content.replace(/@Content\s*\([^)]*\)\s*/g, '');

    // Remove @Operation(...) up to the next valid Spring annotation
    // [\s\S]*? means non-greedy match of any character including newlines
    content = content.replace(/@Operation\s*\([\s\S]*?\)\s*(?=@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|@PatchMapping)/g, '');

    // Remove @ApiResponses(...)
    content = content.replace(/@ApiResponses\s*\([\s\S]*?\)\s*(?=@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|@PatchMapping)/g, '');

    // Remove @ApiResponse(...) standalone (if any)
    content = content.replace(/@ApiResponse\s*\([\s\S]*?\)\s*(?=@GetMapping|@PostMapping|@PutMapping|@DeleteMapping|@PatchMapping)/g, '');

    if (content !== originalContent) {
        fs.writeFileSync(filePath, content, 'utf8');
        console.log('Cleaned: ' + filePath);
    }
}

function walkDir(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            walkDir(fullPath);
        } else if (fullPath.endsWith('.java')) {
            processFile(fullPath);
        }
    }
}

walkDir('src/main/java');

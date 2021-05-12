import * as assert from 'assert';
import * as fs from 'fs';
import * as path from 'path';
import * as ps from 'ps-node';
import { spawn, ChildProcessByStdio, spawnSync, SpawnSyncReturns } from 'child_process';
import { Readable } from 'stream';

// You can import and use all API from the 'vscode' module
// as well as import your extension to test it
import * as vscode from 'vscode';
import * as myExtension from '../../extension';
import { TextDocument, TextEditor, Uri } from 'vscode';

suite('Extension Test Suite', () => {
    vscode.window.showInformationMessage('Cleaning up workspace.');
    let folder: string = assertWorkspace();
    fs.rmdirSync(folder, { recursive: true });
    fs.mkdirSync(folder, { recursive: true });
    vscode.window.showInformationMessage('Start all tests.');
    myExtension.enableConsoleLog();

    test('VSNetBeans is present', async () => {
        let nbcode = vscode.extensions.getExtension('asf.apache-netbeans-java');
        assert.ok(nbcode, "Apache NetBeans Extension is present");
        let api = await nbcode.activate();
        assert.ok(api.version, "Some version is specified");

        let cannotReassignVersion = false;
        try {
            api.version = "different";
        } catch (e) {
            cannotReassignVersion = true;
        }
        assert.ok(cannotReassignVersion, "Cannot reassign value of version");
    });

    test('Find clusters', async () => {
        const nbcode = vscode.extensions.getExtension('asf.apache-netbeans-java');
        assert.ok(nbcode);

        const extraCluster = path.join(nbcode.extensionPath, "nbcode", "extra");
        let clusters = myExtension.findClusters('non-existent').
            // ignore 'extra' cluster in the extension path, since nbjavac is there during development:
            filter(s => !s.startsWith(extraCluster));


        let found : string[] = [];
        function assertCluster(name : string) {
            for (let c of clusters) {
                if (c.endsWith('/' + name)) {
                    found.push(c);
                    return;
                }
            }
            assert.fail(`Cannot find ${name} among ${clusters}`);
        }

        assertCluster('extide');
        assertCluster('ide');
        assertCluster('java');
        assertCluster('nbcode');
        assertCluster('platform');
        assertCluster('webcommon');
        assertCluster('harness');

        for (let c of found) {
            assert.ok(c.startsWith(nbcode.extensionPath), `All extensions are below ${nbcode.extensionPath}, but: ${c}`);
        }
    });

    async function demo(where: number) {
        let folder: string = assertWorkspace();

        await fs.promises.writeFile(path.join(folder, 'pom.xml'), `
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.netbeans.demo.vscode.t1</groupId>
    <artifactId>basicapp</artifactId>
    <version>1.0</version>
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
</project>
		`);

        let pkg = path.join(folder, 'src', 'main', 'java', 'pkg');
        let mainJava = path.join(pkg, 'Main.java');

        await fs.promises.mkdir(pkg, { recursive: true });

        await fs.promises.writeFile(mainJava, `
package pkg;
class Main {
	public static void main(String... args) {
		System.out.println("Hello World!");
	}
}
		`);

        vscode.workspace.saveAll();

        if (where === 6) return;

        try {
            console.log("Test: invoking compile");
            let res = await vscode.commands.executeCommand("java.workspace.compile");
            console.log(`Test: compile finished with ${res}`);
        } catch (error) {
            dumpJava();
            throw error;
        }

        if (where === 7) return;

        let mainClass = path.join(folder, 'target', 'classes', 'pkg', 'Main.class');

        if (where === 8) return;

        assert.ok(fs.statSync(mainClass).isFile(), "Class created by compilation: " + mainClass);
    }

    test("Compile workspace6", async() => demo(6));
    test("Compile workspace7", async() => demo(7));
    test("Compile workspace8", async() => demo(8));

    /**
     * Checks that maven-managed process can be started, and forcefully terminated by vscode
     * although it does not run in debugging mode.
     */
    async function mavenTerminateWithoutDebugger() {
        let folder: string = assertWorkspace();

        await fs.promises.writeFile(path.join(folder, 'pom.xml'), `
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.netbeans.demo.vscode.t1</groupId>
    <artifactId>basicapp</artifactId>
    <version>1.0</version>
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
    </project>
        `);

        let pkg = path.join(folder, 'src', 'main', 'java', 'pkg');
        let mainJava = path.join(pkg, 'Main.java');

        await fs.promises.mkdir(pkg, { recursive: true });

        await fs.promises.writeFile(mainJava, `
    package pkg;
    class Main {
    public static void main(String... args) throws Exception {
        System.out.println("Endless wait...");
        while (true) {
            Thread.sleep(1000);
        }
    }
    }
        `);
        vscode.workspace.saveAll();
        let u : Uri = vscode.Uri.file(mainJava);
        let doc : TextDocument = await vscode.workspace.openTextDocument(u);
        let e : TextEditor = await vscode.window.showTextDocument(doc);

        try {
            let terminated = false;
            let r = new Promise((resolve, reject) => {
                function waitUserApplication(cnt : number, running: boolean, cb : () => void) {
                    ps.lookup({
                        command: "^.*[/\\\\]java",
                        arguments: "pkg.Main"
                    }, (err, list ) => {
                        let success : boolean = (list && list.length > 0) == running;
                        if (success) {
                            cb();
                        } else {
                            if (cnt == 0) {
                                reject(new Error("Timeout waiting for user application"));
                                return;
                            }
                            setTimeout(() => waitUserApplication(cnt - 1, running, cb), 100);
                            return;
                        }
                    });
                }
                
                function onProcessStarted() {
                    console.log("Test: invoking debug.stop");
                    // attempt to terminate:
                    vscode.commands.executeCommand("workbench.action.debug.stop").
                        then(() => waitUserApplication(5, false, () => resolve(true)));
                }
                console.log("Test: invoking debug debug.run");
                const workspaceFolder = (vscode.workspace.workspaceFolders!)[0];
                vscode.debug.startDebugging(workspaceFolder, {type: "java8+", name: "Launch Java 8+ App", request: "launch"}, {}).
                    then(() => waitUserApplication(5, true, onProcessStarted));
            });
            return r;
        } catch (error) {
            dumpJava();
            throw error;
        }
    }

    test("Maven run termination", async() => mavenTerminateWithoutDebugger());
});

function assertWorkspace(): string {
    assert.ok(vscode.workspace, "workspace is defined");
    const dirs = vscode.workspace.workspaceFolders;
    assert.ok(dirs?.length, "There are some workspace folders: " + dirs);
    assert.strictEqual(dirs.length, 1, "One folder provided");
    let folder: string = dirs[0].uri.fsPath;
    return folder;
}

async function dumpJava() {
    const cmd = 'jps';
    const args = [ '-v' ];
    console.log(`Running: ${cmd} ${args.join(' ')}`);
    let p : ChildProcessByStdio<null, Readable, Readable> = spawn(cmd, args, {
        stdio : ["ignore", "pipe", "pipe"],
    });
    let n = await new Promise<number>((r, e) => {
        p.stdout.on('data', function(d: any) {
            console.log(d.toString());
        });
        p.stderr.on('data', function(d: any) {
            console.log(d.toString());
        });
        p.on('close', function(code: number) {
            r(code);
        });
    });
    console.log(`${cmd} ${args.join(' ')} finished with code ${n}`);
}

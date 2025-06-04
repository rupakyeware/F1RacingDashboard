{pkgs}: {
  deps = [
    pkgs.postgresql
    pkgs.curl
    pkgs.wget
    pkgs.maven
    pkgs.jdk
  ];
}

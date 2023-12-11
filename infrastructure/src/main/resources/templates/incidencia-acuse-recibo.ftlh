<html lang="en">
<head>
    <meta charset="UTF-8"></meta>
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"></meta>
    <meta http-equiv="X-UA-Compatible" content="ie=edge"></meta>
    <title>Nueva empresa dentro</title>
    <style type="text/css">
        @page {
            size: a4;
            margin: 10px;
            @bottom-center {
                content: 'Pagina ' counter(page) ' de ' counter(pages);
            }
        }

        html {
            width: 100%;
            font-family: Arial, Helvetica, sans-serif;
            font-size: 13px;
        }

        body {
            width: 100%;
        }

        .col-50 {
            width: 50%;
            margin: 0;
            float: left;
        }

        .col-48 {
            width: 48%;
            margin: 0;
            float: left;
        }

        .col-25 {
            width: 25%;
            margin: 0;
            float: left;
        }

        .col-75 {
            width: 75%;
            margin: 0;
            float: left;
        }

        .col-80 {
            width: 80%;
            margin: 0;
            float: left;
        }

        .col-18 {
            width: 18%;
            margin: 0;
            float: left;
        }

        .col-100 {
            width: 100%;
            margin: 0;
            float: left;
        }

        .col-98 {
            width: 98%;
            margin: 0;
            float: left;
        }

        .row {
            width: 100%;
            min-height: 20px;
            float: left;
        }

        page-after {
            page-break-after: always;
        }

        page-before {
            page-break-before: always;
        }

        spacer {
            page-break-before: always;
            display: block;
        }
    </style>
</head>
<body class="">
<div class="row">
    <div class="col-98">
        <a href="https://postdrop.io"><img src="https://pelisat.com.mx/logo-ajustado.png" height="60" alt="Postdrop"></img></a>
    </div>
</div>
<div class="row">
    <div class="col-98">
        <b>DIRECCION DE LOS SERVICIOS DE SEGURIDAD PRIVADA DEL SECRETARIADO EJECUTIVO DEL CONSEJO ESTATAL DE SEGURIDAD PUBLICA DEL ESTADO DE JALISCO:</b>
    </div>
</div>
<div class="row">
    <div class="col-98">
        <p style="text-align: justify">El presente se emite como acuse de recibo de la incidencia generada por el prestador de servicios de seguridad privada en la plataforma electrónica denominada Sistema Integral de Empresas de Seguridad Privada "ARGOS", en la cual se hace constar la siguiente información:</p>
    </div>
</div>
<div class="row">
    <div class="col-98">
        <p style="text-align: justify;">Nombre del prestador de servicios: <br></br><b>${nombrePrestadorServicio}</b></p>
        <p style="text-align: justify;">Numero de expediente: <br></br><b>${numeroExpediente}</b></p>
        <p style="text-align: justify;">Tipo de actuación: <br></br><b>PROCEDENTE</b></p>
        <p style="text-align: justify;">Fecha y hora de generacion: <br></br><b>${fechaHoraGeneracion}</b></p>
        <p style="text-align: justify;">Cuenta de correo electrónico determinado por el prestador de servicios: <br></br><b>${correo}</b></p>
        <p style="text-align: justify;">Numero de acuse de recibo: <br></br><b>${numeroAcuseRecibo}</b></p>
    </div>
</div>

<div class="row">
    <div class="col-98">
        <p style="text-align: justify;">El presente acuse electronico se genera en atención a lo dispuesto por los artículos 55 fracciones III y IV y 161 del Reglamento de los Servicios de Seguridad Privada del Estado de Jalisco.</p>
    </div>
</div>

<div class="row">
    <div class="col-98">
        <p>Cadena Original: <br></br><b>${cadenaOriginal}</b></p>
        <p style="word-break: break-all !important;">Sello: <br></br><b>${sello}</b></p>
    </div>
</div>

<div class="row">
    <div class="col-98">
        <p><img src="${imagenBase64}" alt="Codigo QR"></img></p>
    </div>
</div>
</body>
</html>
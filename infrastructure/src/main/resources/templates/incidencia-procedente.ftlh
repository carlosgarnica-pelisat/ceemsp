<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Nueva empresa dentro</title>
    <style type="text/css">
        img {
            border: none;
            -ms-interpolation-mode: bicubic;
            max-width: 100%;
        }

        body {
            background-color: #eaebed;
            font-family: sans-serif;
            -webkit-font-smoothing: antialiased;
            font-size: 14px;
            line-height: 1.4;
            margin: 0;
            padding: 0;
            -ms-text-size-adjust: 100%;
            -webkit-text-size-adjust: 100%;
        }

        table {
            border-collapse: separate;
            mso-table-lspace: 0pt;
            mso-table-rspace: 0pt;
            min-width: 100%;
            width: 100%; }
        table td {
            font-family: sans-serif;
            font-size: 14px;
            vertical-align: top;
        }

        /* -------------------------------------
            BODY & CONTAINER
        ------------------------------------- */

        .body {
            background-color: #eaebed;
            width: 100%;
        }

        /* Set a max-width, and make it display as block so it will automatically stretch to that width, but will also shrink down on a phone or something */
        .container {
            display: block;
            Margin: 0 auto !important;
            /* makes it centered */
            max-width: 700px;
            padding: 10px;
            width: 700px;
        }

        /* This should also be a block element, so that it will fill 100% of the .container */
        .content {
            box-sizing: border-box;
            display: block;
            Margin: 0 auto;
            max-width: 700px;
            padding: 10px;
        }

        /* -------------------------------------
            HEADER, FOOTER, MAIN
        ------------------------------------- */
        .main {
            background: #ffffff;
            border-radius: 3px;
            width: 100%;
        }

        .header {
            padding: 20px 0;
        }

        .wrapper {
            box-sizing: border-box;
            padding: 20px;
        }

        .content-block {
            padding-bottom: 10px;
            padding-top: 10px;
        }

        .footer {
            clear: both;
            Margin-top: 10px;
            text-align: center;
            width: 100%;
        }
        .footer td,
        .footer p,
        .footer span,
        .footer a {
            color: #9a9ea6;
            font-size: 12px;
            text-align: center;
        }

        /* -------------------------------------
            TYPOGRAPHY
        ------------------------------------- */
        h1,
        h2,
        h3,
        h4 {
            color: #06090f;
            font-family: sans-serif;
            font-weight: 400;
            line-height: 1.4;
            margin: 0;
            margin-bottom: 30px;
        }

        h1 {
            font-size: 35px;
            font-weight: 300;
            text-align: center;
            text-transform: capitalize;
        }

        p,
        ul,
        ol {
            font-family: sans-serif;
            font-size: 14px;
            font-weight: normal;
            margin: 0;
            margin-bottom: 15px;
        }
        p li,
        ul li,
        ol li {
            list-style-position: inside;
            margin-left: 5px;
        }

        a {
            color: #405f95;
            text-decoration: underline;
        }

        /* -------------------------------------
            BUTTONS
        ------------------------------------- */
        .btn {
            box-sizing: border-box;
            width: 100%; }
        .btn > tbody > tr > td {
            padding-bottom: 15px; }
        .btn table {
            min-width: auto;
            width: auto;
        }
        .btn table td {
            background-color: #ffffff;
            border-radius: 5px;
            text-align: center;
        }
        .btn a {
            background-color: #ffffff;
            border: solid 1px #405f95;
            border-radius: 5px;
            box-sizing: border-box;
            color: #405f95;
            cursor: pointer;
            display: inline-block;
            font-size: 14px;
            font-weight: bold;
            margin: 0;
            padding: 12px 25px;
            text-decoration: none;
            text-transform: capitalize;
        }

        .btn-primary table td {
            background-color: #405f95;
        }

        .btn-primary a {
            background-color: #405f95;
            border-color: #405f95;
            color: #ffffff;
        }

        /* -------------------------------------
            OTHER STYLES THAT MIGHT BE USEFUL
        ------------------------------------- */
        .last {
            margin-bottom: 0;
        }

        .first {
            margin-top: 0;
        }

        .align-center {
            text-align: center;
        }

        .align-right {
            text-align: right;
        }

        .align-left {
            text-align: left;
        }

        .clear {
            clear: both;
        }

        .mt0 {
            margin-top: 0;
        }

        .mb0 {
            margin-bottom: 0;
        }

        .preheader {
            color: transparent;
            display: none;
            height: 0;
            max-height: 0;
            max-width: 0;
            opacity: 0;
            overflow: hidden;
            mso-hide: all;
            visibility: hidden;
            width: 0;
        }

        .powered-by a {
            text-decoration: none;
        }

        hr {
            border: 0;
            border-bottom: 1px solid #f6f6f6;
            Margin: 20px 0;
        }

        /* -------------------------------------
            RESPONSIVE AND MOBILE FRIENDLY STYLES
        ------------------------------------- */
        @media only screen and (max-width: 720px) {
            table[class=body] h1 {
                font-size: 28px !important;
                margin-bottom: 10px !important;
            }
            table[class=body] p,
            table[class=body] ul,
            table[class=body] ol,
            table[class=body] td,
            table[class=body] span,
            table[class=body] a {
                font-size: 16px !important;
            }
            table[class=body] .wrapper,
            table[class=body] .article {
                padding: 10px !important;
            }
            table[class=body] .content {
                padding: 0 !important;
            }
            table[class=body] .container {
                padding: 0 !important;
                width: 100% !important;
            }
            table[class=body] .main {
                border-left-width: 0 !important;
                border-radius: 0 !important;
                border-right-width: 0 !important;
            }
            table[class=body] .btn table {
                width: 100% !important;
            }
            table[class=body] .btn a {
                width: 100% !important;
            }
            table[class=body] .img-responsive {
                height: auto !important;
                max-width: 100% !important;
                width: auto !important;
            }
        }

        /* -------------------------------------
            PRESERVE THESE STYLES IN THE HEAD
        ------------------------------------- */
        @media all {
            .ExternalClass {
                width: 100%;
            }
            .ExternalClass,
            .ExternalClass p,
            .ExternalClass span,
            .ExternalClass font,
            .ExternalClass td,
            .ExternalClass div {
                line-height: 100%;
            }
            .apple-link a {
                color: inherit !important;
                font-family: inherit !important;
                font-size: inherit !important;
                font-weight: inherit !important;
                line-height: inherit !important;
                text-decoration: none !important;
            }
            .btn-primary table td:hover {
                background-color: #405f95 !important;
            }
            .btn-primary a:hover {
                background-color: #405f95 !important;
                border-color: #405f95 !important;
            }
        }
    </style>
</head>
<body class="">
<table role="presentation" border="0" cellpadding="0" cellspacing="0" class="body">
    <tr>
        <td class="container">
            <div class="header">
                <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td class="align-center" width="100%">
                            <a href="https://postdrop.io"><img src="https://pelisat.com.mx/logo-ajustado.png" height="60" alt="Postdrop"></a>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="content">

                <!-- START CENTERED WHITE CONTAINER -->
                <span class="preheader">Aviso de notificación.</span>
                <table role="presentation" class="main">

                    <!-- START MAIN CONTENT AREA -->
                    <tr>
                        <td class="wrapper">
                            <table role="presentation" border="0" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td>
                                        <h2>Acuse de Recibo</h2>
                                        <hr>
                                        <p style="text-align: justify;">El presente se emite como acuse de recibo de la incidencia generada por el prestador de servicios de seguridad privada en la plataforma electrónica denominada Sistema Integral de Empresas de Seguridad Privada "ARGOS", en la cual se hace constar la siguiente información:<p>
                                            <br>
                                        <p style="text-align: justify;">Nombre del prestador de servicios: <br><b>${nombrePrestadorServicio}</b></p>
                                        <p style="text-align: justify;">Numero de expediente: <br><b>${numeroExpediente}</b></p>
                                        <p style="text-align: justify;">Tipo de actuación: <br><b>PROCEDENTE</b></p>
                                        <p style="text-align: justify;">Fecha y hora de generacion: <br><b>${fechaHoraGeneracion}</b></p>
                                        <p style="text-align: justify;">Cuenta de correo electrónico determinado por el prestador de servicios: <br><b>${correo}</b></p>
                                        <p style="text-align: justify;">El presente acuse electronico se genera en atención a lo dispuesto por los artículos 55 fracciones III y IV y 161 del Reglamento de los Servicios de Seguridad Privada del Estado de Jalisco.<p>
                                        <p style="text-align: justify;">Numero de notificacion: <br><b>${numeroNotificacion}</b></p>
                                        <p style="text-align: justify;">Cadena Original: <br><b>${cadenaOriginal}</b></p>
                                        <p style="text-align: justify;">Sello: <br><b>${sello}</b></p>
                                        <hr>
                                        <p style="text-align: justify;">Recuerda siempre que ni el CEEMSP ni ningun organismo perteneciente al Consejo Estatal de Seguridad Privada (CESP) te requerira bajo ningun motivo informacion personal o confidencial como contraseñas, codigos, claves o demas informacion afin a traves de correo electronico.</p>
                                        <p style="text-align: justify;">En caso de recibir algun correo en donde te solicite dicha informacion o bien, ejecutar algun software o algun archivo adjunto no identificado, te solicitamos no hacer ninguna accion de este tipo y reportarlo a traves de nuestros canales de comunicacion oficiales.</p>
                                        <p style="text-align: justify;">Este mensaje y cualquier archivo adjunto al mismo pueden contener información que podría considerarse confidencial y/o reservada, Si ha recibido el mensaje por error, por favor notifique al remitente contestando al correo, y destruyendo el mensaje original y sus anexos, como una medida de seguridad de carácter administrativo, de conformidad a lo establecido en la Ley General de Protección de Datos Personales en Posesión de Sujetos Obligados</p>
                                        <hr>
                                        <p style="text-align: center;"><b>DIRECCIÓN DE LOS SERVICIOS DE SEGURIDAD PRIVADA DEL SECRETARIADO EJECUTIVO DEL CONSEJO ESTATAL DE SEGURIDAD PÚBLICA DEL ESTADO DE JALISCO</b></p>
                                        <p style="text-align: center;">Avenida La Paz 875, Segundo Piso. Colonia Mexicaltzingo, C. P. 44180, Guadalajara, Jalisco, Mexico. <br>Tels. (33) 363022776 y (33) 38181480</p>
                                        <p style="text-align: center;"><a href="https://transparenciasitgej.jalisco.gob.mx/api/api/banco_archivos/19018/downloadWeb">Aviso de privacidad</a></p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                    <!-- END MAIN CONTENT AREA -->
                </table>

                <!-- START FOOTER -->
                <div class="footer">
                    <table role="presentation" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td class="content-block">
                                <span class="apple-link">Don't forget to add your address here</span>
                                <br> And <a href="https://postdrop.io">unsubscribe link</a> here.
                            </td>
                        </tr>
                        <tr>
                            <td class="content-block powered-by">
                                Powered by <a href="https://postdrop.io">Postdrop</a>.
                            </td>
                        </tr>
                    </table>
                </div>
                <!-- END FOOTER -->

                <!-- END CENTERED WHITE CONTAINER -->
            </div>
        </td>
    </tr>
</table>
</body>
</html>